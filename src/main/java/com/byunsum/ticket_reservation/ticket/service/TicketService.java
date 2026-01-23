package com.byunsum.ticket_reservation.ticket.service;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.global.monitoring.SlackNotifier;
import com.byunsum.ticket_reservation.reservation.domain.pre.Reservation;
import com.byunsum.ticket_reservation.reservation.domain.reservation.ReservationSeat;
import com.byunsum.ticket_reservation.reservation.repository.ReservationRepository;
import com.byunsum.ticket_reservation.ticket.domain.*;
import com.byunsum.ticket_reservation.ticket.dto.TicketVerifyResponse;
import com.byunsum.ticket_reservation.ticket.qr.QrCodeGenerator;
import com.byunsum.ticket_reservation.ticket.repository.TicketReissueLogRepository;
import com.byunsum.ticket_reservation.ticket.repository.TicketRepository;
import com.byunsum.ticket_reservation.ticket.repository.TicketVerificationLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final ReservationRepository reservationRepository;
    private final StringRedisTemplate stringRedisTemplate;
    private final QrCodeGenerator qrCodeGenerator;

    private static final Duration TICKET_VALID_DURATION = Duration.ofHours(4);
    private static final String REDIS_KEY_PREFIX = "ticket:";
    private static final String LOCK_KEY_PREFIX = "lock:ticket:";

    private final TicketVerificationLogRepository ticketVerificationLogRepository;
    private final SlackNotifier slackNotifier;
    private final TicketReissueLogRepository ticketReissueLogRepository;

    private final ObjectMapper objectMapper;

    public TicketService(TicketRepository ticketRepository, ReservationRepository reservationRepository, QrCodeGenerator qrCodeGenerator, StringRedisTemplate stringRedisTemplate, TicketVerificationLogRepository ticketVerificationLogRepository, SlackNotifier slackNotifier, TicketReissueLogRepository ticketReissueLogRepository, ObjectMapper objectMapper) {
        this.ticketRepository = ticketRepository;
        this.reservationRepository = reservationRepository;
        this.qrCodeGenerator = qrCodeGenerator;
        this.stringRedisTemplate = stringRedisTemplate;
        this.ticketVerificationLogRepository = ticketVerificationLogRepository;
        this.slackNotifier = slackNotifier;
        this.ticketReissueLogRepository = ticketReissueLogRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public List<Ticket> issueTickets(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        List<Ticket> tickets = new ArrayList<>();

        for(ReservationSeat rs : reservation.getReservationSeats()) {
            if(ticketRepository.existsByReservationSeatIdAndStatus(rs.getId(), TicketStatus.ISSUED)) {
                throw new CustomException(ErrorCode.TICKET_ALREADY_ISSUED);
            }

            String ticketCode = UUID.randomUUID().toString();
            String qrImageUrl = qrCodeGenerator.generate(ticketCode);

            Ticket ticket = Ticket.create(rs, ticketCode, qrImageUrl, TICKET_VALID_DURATION);
            ticketRepository.save(ticket);

            stringRedisTemplate.opsForValue().set(
                    REDIS_KEY_PREFIX + ticketCode,
                    rs.getId().toString(),
                    TICKET_VALID_DURATION.toMinutes(),
                    TimeUnit.MINUTES
            );

            tickets.add(ticket);
        }

        return tickets;
    }

    @Transactional(readOnly = true)
    public List<Ticket> getTicketsByReservationId(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        List<Ticket> tickets = new ArrayList<>();

        for(ReservationSeat rs : reservation.getReservationSeats()) {
            ticketRepository.findFirstByReservationSeatIdOrderByIssuedAtDesc(rs.getId()).ifPresent(tickets::add);
        }

        return tickets;
    }

    @Transactional
    public Ticket refreshQrCode(Long reservationSeatId) {
        Ticket current = ticketRepository.findFirstByReservationSeatIdAndStatusOrderByIssuedAtDesc(reservationSeatId, TicketStatus.ISSUED)
                .orElseThrow(() -> new CustomException(ErrorCode.TICKET_NOT_FOUND));

        LocalDateTime roundStart = current.getReservationSeat()
                .getSeat().getPerformanceRound().getStartDateTime();

        if (LocalDateTime.now().isBefore(roundStart.minusHours(3))) {
            throw new CustomException(ErrorCode.QR_NOT_YET_AVAILABLE);
        }

        String oldCode = current.getTicketCode();

        //기존티켓 무효화
        current.invalidate();
        ticketRepository.save(current);

        String newCode = UUID.randomUUID().toString();
        String newQrImage = qrCodeGenerator.generate(newCode);

        Ticket newTicket = Ticket.create(
                current.getReservationSeat(),
                newCode,
                newQrImage,
                Duration.ofMinutes(30)
        );

        ticketRepository.save(newTicket);

        stringRedisTemplate.opsForValue().set(
                REDIS_KEY_PREFIX + newCode,
                reservationSeatId.toString(),
                30, TimeUnit.MINUTES
        );

        LocalDateTime roundEnd = current.getReservationSeat()
                .getSeat().getPerformanceRound().getEndDateTime();

        if (roundEnd == null) {
            throw new CustomException(ErrorCode.ROUND_NOT_FOUND);
        }

        long expiresAtMillis = roundEnd.plusDays(1)
                .atZone(ZoneId.systemDefault())
                .toInstant().toEpochMilli();

        String blacklistKey = "blacklist:ticket:" + oldCode;
        stringRedisTemplate.opsForValue().set(blacklistKey, "true");
        stringRedisTemplate.expireAt(blacklistKey, new Date(expiresAtMillis));

        String loginId = current.getReservationSeat().getReservation().getMember().getLoginId();
        String username = current.getReservationSeat().getReservation().getMember().getUsername();

        ticketReissueLogRepository.save(
                new TicketReissueLog(current.getReservationSeat(), oldCode, newCode, loginId, username, ReissueReason.USER_REQUEST, LocalDateTime.now())
        );

        return newTicket;
    }


    @Transactional
    public List<Ticket> refreshQrCodes(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        if(reservation.getReservationSeats().isEmpty()) {
            throw new CustomException(ErrorCode.SEAT_NOT_FOUND);
        }

        LocalDateTime roundStart = reservation.getReservationSeats().get(0)
                .getSeat().getPerformanceRound().getStartDateTime();

        if(LocalDateTime.now().isBefore(roundStart.minusHours(3))) {
            throw new CustomException(ErrorCode.QR_NOT_YET_AVAILABLE);
        }

        List<Ticket> tickets = new ArrayList<>();

        for(ReservationSeat rs : reservation.getReservationSeats()) {
            Ticket current = ticketRepository.findFirstByReservationSeatIdAndStatusOrderByIssuedAtDesc(rs.getId(), TicketStatus.ISSUED)
                    .orElseThrow(() -> new CustomException(ErrorCode.TICKET_NOT_FOUND));

            String oldCode = current.getTicketCode();

            //기존 티켓 무효화
            current.invalidate();
            ticketRepository.save(current);

            String newCode = UUID.randomUUID().toString();
            String newQrImage = qrCodeGenerator.generate(newCode);

            Ticket newTicket = Ticket.create(
                    current.getReservationSeat(),
                    newCode,
                    newQrImage,
                    Duration.ofMinutes(30)
            );
            ticketRepository.save(newTicket);

            stringRedisTemplate.opsForValue().set(
                    REDIS_KEY_PREFIX + newCode,
                    rs.getId().toString(),
                    30, TimeUnit.MINUTES
            );

            LocalDateTime roundEnd = rs.getSeat().getPerformanceRound().getEndDateTime();

            if(roundEnd == null) {
                throw new CustomException(ErrorCode.ROUND_NOT_FOUND);
            }

            long expiresAtMillis = roundEnd.plusDays(1)
                    .atZone(ZoneId.systemDefault())
                    .toInstant().toEpochMilli();

            String blacklistKey = "blacklist:ticket:" + oldCode;
            stringRedisTemplate.opsForValue().set(blacklistKey, "true");
            stringRedisTemplate.expireAt(blacklistKey, new Date(expiresAtMillis));

            String loginId = rs.getReservation().getMember().getLoginId();
            String username = rs.getReservation().getMember().getUsername();

            ticketReissueLogRepository.save(
                    new TicketReissueLog(rs, oldCode, newCode, loginId, username, ReissueReason.USER_REQUEST, LocalDateTime.now())
            );

            tickets.add(newTicket);
        }

        return tickets;
    }

    @Transactional
    public TicketVerifyResponse verifyTicket(String ticketCode, HttpServletRequest request) {
        String cacheKey = "verifyTicket:" + ticketCode;

        TicketVerifyResponse cached = readCachedVerifyResponse(cacheKey);
        if (cached != null) return cached;

        if (!acquireVerifyLock(ticketCode)) {
            String verifier = SecurityContextHolder.getContext().getAuthentication().getName();
            String ipAddress = clientIp(request);
            String userAgent = request.getHeader("User-Agent");

            TicketVerifyResponse response = build(
                    false, TicketVerifyResult.DUPLICATE_SCAN.name(), "이미 검표가 진행 중인 티켓입니다.",
                    null, null, null,
                    verifier, userAgent, LocalDateTime.now()
            );

            saveLogAndCache(ticketCode, response, cacheKey, ipAddress);
            return response;
        }

        try {
            TicketVerifyResponse cached2 = readCachedVerifyResponse(cacheKey);
            if(cached2 != null) return cached2;

            String verifier = SecurityContextHolder.getContext().getAuthentication().getName();
            String ipAddress = clientIp(request);
            String userAgent = request.getHeader("User-Agent");
            LocalDateTime verifiedAt = LocalDateTime.now();

            //1. 블랙리스트 조회
            String blacklistKey = "blacklist:ticket:" + ticketCode;

            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(blacklistKey))) {
                TicketVerifyResponse response = build(
                        false, TicketVerifyResult.INVALIDATED.name(), "무효 처리된 티켓입니다.",
                        null, null, null,
                        verifier, userAgent, verifiedAt);

                saveLogAndCache(ticketCode, response, cacheKey, ipAddress);

                safeSlack(String.format(
                        "취소/무효 티켓 검증 시도 감지\n검증자: %s\n티켓코드: %s\nUA:%s\nIP: %s",
                        verifier, ticketCode, userAgent, ipAddress
                ));

                return response;
            }

            //2. Redis에서 티켓 유효성 확인
            String redisKey = REDIS_KEY_PREFIX + ticketCode;
            String reservationSeatId = stringRedisTemplate.opsForValue().get(redisKey);

            if (reservationSeatId == null) {
                TicketVerifyResponse response = build(
                        false, TicketVerifyResult.EXPIRED.name(), "티켓이 만료되었습니다. 재발급이 필요합니다.",
                        null, null, null,
                        verifier, userAgent, verifiedAt);

                saveLogAndCache(ticketCode, response, cacheKey, ipAddress);

                return response;
            }

            //3. DB에서 티켓 조회
            Ticket ticket;

            try {
                ticket = ticketRepository.findByTicketCode(ticketCode)
                        .orElseThrow(() -> new CustomException(ErrorCode.TICKET_NOT_FOUND));
            } catch (CustomException e) {
                if(e.getErrorCode() == ErrorCode.TICKET_NOT_FOUND) {
                    TicketVerifyResponse response = build(
                            false, TicketVerifyResult.NOT_FOUND.name(), "티켓을 찾을 수 없습니다.",
                            null, null, null,
                            verifier, userAgent, verifiedAt
                    );

                    saveLogAndCache(ticketCode, response, cacheKey, ipAddress);

                    return response;
                }

                throw e;
            }

            Long dbReservationSeatId = ticket.getReservationSeat() != null ? ticket.getReservationSeat().getId() : null;

            if (dbReservationSeatId == null || !reservationSeatId.equals(dbReservationSeatId.toString())) {
                TicketVerifyResponse response = build(
                        false, TicketVerifyResult.TAMPERED.name(), "티켓 정보가 유효하지 않습니다. 관리자에게 문의해주세요.",
                        null, null, null,
                        verifier, userAgent, verifiedAt
                );

                saveLogAndCache(ticketCode, response, cacheKey, ipAddress);

                safeSlack(String.format(
                        "티켓 매핑 불일치 감지\n검증자: %s\n티켓코드: %s\nRedis reservationSeatId: %s\nDB reservationSeatId: %s\nUA:%s\nIP: %s",
                        verifier, ticketCode, reservationSeatId, dbReservationSeatId, userAgent, ipAddress
                ));

                return response;
            }

            String performanceTitle = ticket.getReservationSeat()
                    .getReservation().getPerformance().getTitle();
            String seatNo = ticket.getReservationSeat().getSeat().getSeatNo();
            LocalDateTime expiresAt = ticket.getExpiresAt();

            //4. 상태별 검표 처리
            if (ticket.getStatus() == TicketStatus.USED) {
                TicketVerifyResponse response = build(
                        false, TicketVerifyResult.USED.name(), "이미 사용된 티켓입니다.",
                        performanceTitle, seatNo, expiresAt,
                        verifier, userAgent, verifiedAt);

                saveLogAndCache(ticketCode, response, cacheKey, ipAddress);

                return  response;
            }

            if (ticket.getStatus() == TicketStatus.INVALIDATED) {
                TicketVerifyResponse response = build(
                        false, TicketVerifyResult.INVALIDATED.name(), "무효 처리된 티켓입니다.",
                        performanceTitle, seatNo, expiresAt,
                        verifier, userAgent, verifiedAt);

                saveLogAndCache(ticketCode, response, cacheKey, ipAddress);

                safeSlack(String.format(
                        "무효 처리된 티켓 검증 시도 감지\n검증자: %s\n티켓코드: %s\nUA:%s\nIP: %s",
                        verifier, ticketCode, userAgent, ipAddress
                ));

                return  response;
            }

            if (ticket.getExpiresAt() != null && ticket.getExpiresAt().isBefore(LocalDateTime.now())) {
                TicketVerifyResponse response = build(
                        false, TicketVerifyResult.EXPIRED.name(), "티켓이 만료되었습니다.",
                        performanceTitle, seatNo, expiresAt,
                        verifier, userAgent, verifiedAt);

                saveLogAndCache(ticketCode, response, cacheKey, ipAddress);

                return  response;
            }

            int updated = ticketRepository.markUsedIfIssued(ticketCode);

            if(updated == 1) {
                TicketVerifyResponse response = build(
                        true, TicketVerifyResult.SUCCESS.name(), "입장 완료",
                        performanceTitle, seatNo, expiresAt,
                        verifier, userAgent, verifiedAt);

                saveLogAndCache(ticketCode, response, cacheKey, ipAddress);

                return  response;
            }

            //updated == 0이면 이미 USED/INVALIDATED/EXPIRED 등일 확률
            Ticket latest = ticketRepository.findByTicketCode(ticketCode)
                    .orElseThrow(() -> new CustomException(ErrorCode.TICKET_NOT_FOUND));

            TicketStatus latestStatus = latest.getStatus();
            TicketVerifyResponse response;

            if(latestStatus == TicketStatus.USED) {
                response = build(
                        false, TicketVerifyResult.USED.name(), "이미 사용된 티켓입니다.",
                        performanceTitle, seatNo, expiresAt,
                        verifier, userAgent, verifiedAt
                );
            } else if(latestStatus == TicketStatus.INVALIDATED) {
                response = build(
                        false, TicketVerifyResult.INVALIDATED.name(), "무효 처리된 티켓입니다.",
                        performanceTitle, seatNo, expiresAt,
                        verifier, userAgent, verifiedAt
                );
            } else {
                response = build(
                        false, TicketVerifyResult.INVALID_STATE.name(), "티켓 상태가 유효하지 않습니다.",
                        performanceTitle, seatNo, expiresAt,
                        verifier, userAgent, verifiedAt
                );
            }

            saveLogAndCache(ticketCode, response, cacheKey, ipAddress);

            return response;
        } finally {
            releaseVerifyLock(ticketCode);
        }


    }

    private String clientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");

        if(xff != null && !xff.isBlank()) {
            int comma = xff.indexOf(',');

            return (comma > 0 ? xff.substring(0, comma) : xff).trim();
        }

        return request.getRemoteAddr();
    }

    @Transactional
    public void cancelTicket(String ticketCode) {
        Ticket ticket = ticketRepository.findByTicketCode(ticketCode)
                .orElseThrow(() -> new CustomException(ErrorCode.TICKET_NOT_FOUND));

        ticket.invalidate();

        //Redis TTl 삭제
        stringRedisTemplate.delete(REDIS_KEY_PREFIX + ticketCode);

        LocalDateTime roundEnd = ticket.getReservationSeat()
                .getSeat().getPerformanceRound().getEndDateTime();

        if(roundEnd == null) {
            throw new CustomException(ErrorCode.ROUND_NOT_FOUND);
        }

        long expiresAtMillis = roundEnd.plusDays(1)
                .atZone(ZoneId.systemDefault())
                .toInstant().toEpochMilli();

        //블랙리스트 키 등록
        String blacklistKey = "blacklist:ticket:" + ticketCode;
        stringRedisTemplate.opsForValue().set(blacklistKey, "true");
        stringRedisTemplate.expireAt(blacklistKey, new java.util.Date(expiresAtMillis));
    }

    private void cachedVerifyResponse(String cacheKey, TicketVerifyResponse responseDto) {
        try {
            stringRedisTemplate.opsForValue().set(
                    cacheKey,
                    objectMapper.writeValueAsString(responseDto),
                    5, TimeUnit.SECONDS
            );
        } catch (Exception e) {}
    }

    private boolean acquireVerifyLock(String ticketCode) {
        String lockKey = LOCK_KEY_PREFIX + ticketCode;
        return Boolean.TRUE.equals(
                stringRedisTemplate.opsForValue()
                        .setIfAbsent(lockKey, "LOCK", 4, TimeUnit.SECONDS)
        );
    }

    private void releaseVerifyLock(String ticketCode) {
        stringRedisTemplate.delete(LOCK_KEY_PREFIX + ticketCode);
    }

    private TicketVerifyResponse readCachedVerifyResponse(String cacheKey) {
        String cached = stringRedisTemplate.opsForValue().get(cacheKey);
        if(cached == null) return null;

        try {
            return objectMapper.readValue(cached, TicketVerifyResponse.class);
        } catch (Exception e) {
            return null;
        }
    }

    private TicketVerifyResponse build(
            boolean success,
            String status,
            String message,
            String performanceTitle,
            String seatNo,
            LocalDateTime expiresAt,
            String verifier,
            String deviceInfo,
            LocalDateTime verifiedAt
    ) {
        return new TicketVerifyResponse(
                success,
                status,
                message,
                performanceTitle,
                seatNo,
                expiresAt,
                verifier,
                deviceInfo,
                verifiedAt
        );
    }

    private void saveLogAndCache(String ticketCode, TicketVerifyResponse response, String cacheKey, String ipAddress) {
        LocalDateTime verifiedAt = response.verifiedAt() != null ? response.verifiedAt() : LocalDateTime.now();

        ticketVerificationLogRepository.save(
                new TicketVerificationLog(ticketCode, response.verifier(), ipAddress, toResult(response.status()), verifiedAt)
        );
        cachedVerifyResponse(cacheKey, response);
    }

    private void safeSlack(String message) {
        try {
            slackNotifier.send(message);
        } catch (Exception ignored) {}
    }

    private TicketVerifyResult toResult(String status) {
        try {
            return TicketVerifyResult.valueOf(status);
        } catch (Exception e) {
            return TicketVerifyResult.INVALID_STATE;
        }
    }
}
