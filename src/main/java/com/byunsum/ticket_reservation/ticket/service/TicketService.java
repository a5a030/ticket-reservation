package com.byunsum.ticket_reservation.ticket.service;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.global.monitoring.SlackNotifier;
import com.byunsum.ticket_reservation.reservation.domain.Reservation;
import com.byunsum.ticket_reservation.reservation.domain.ReservationSeat;
import com.byunsum.ticket_reservation.reservation.repository.ReservationRepository;
import com.byunsum.ticket_reservation.reservation.repository.ReservationSeatRepository;
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
    private final ReservationSeatRepository reservationSeatRepository;
    private final StringRedisTemplate stringRedisTemplate;
    private final QrCodeGenerator qrCodeGenerator;

    private static final Duration TICKET_VALID_DURATION = Duration.ofHours(4);
    private static final String REDIS_KEY_PREFIX = "ticket:";
    private static final String LOCK_KEY_PREFIX = "lock:ticket:";

    private final TicketVerificationLogRepository ticketVerificationLogRepository;
    private final SlackNotifier slackNotifier;
    private final TicketReissueLogRepository ticketReissueLogRepository;

    private final ObjectMapper objectMapper;

    public TicketService(TicketRepository ticketRepository, ReservationRepository reservationRepository, ReservationSeatRepository reservationSeatRepository, QrCodeGenerator qrCodeGenerator, StringRedisTemplate stringRedisTemplate, TicketVerificationLogRepository ticketVerificationLogRepository, SlackNotifier slackNotifier, TicketReissueLogRepository ticketReissueLogRepository, ObjectMapper objectMapper) {
        this.ticketRepository = ticketRepository;
        this.reservationRepository = reservationRepository;
        this.reservationSeatRepository = reservationSeatRepository;
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
        //검표 동시성 락
        if(!acquireVerifyLock(ticketCode)) {
            TicketVerifyResponse response = new TicketVerifyResponse(
                    false,
                    "DUPLICATE_SCAN",
                    "이미 검표가 진행 중인 티켓입니다.",
                    null, null, null,
                    SecurityContextHolder.getContext().getAuthentication().getName(),
                    clientIp(request),
                    LocalDateTime.now()
            );

            ticketVerificationLogRepository.save(
                    new TicketVerificationLog(
                            ticketCode,
                            response.verifier(),
                            response.deviceInfo(),
                            "DUPLICATE_SCAN",
                            response.verifiedAt()
                    )
            );

            return response;
        }

        try {
            String cacheKey = "verifyTicket:" + ticketCode;

            String cached = stringRedisTemplate.opsForValue().get(cacheKey);

            if (cached != null) {
                try {
                    return objectMapper.readValue(cached, TicketVerifyResponse.class);
                } catch (Exception ignored) {
                }
            }

            String redisKey = REDIS_KEY_PREFIX + ticketCode;

            String verifier = SecurityContextHolder.getContext().getAuthentication().getName();
            String deviceInfo = clientIp(request);
            String resultStatus;
            TicketVerifyResponse responseDto;

            LocalDateTime verifiedAt = LocalDateTime.now();

            //1. 블랙리스트 조회
            String blacklistKey = "blacklist:ticket:" + ticketCode;

            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(blacklistKey))) {
                resultStatus = TicketStatus.INVALIDATED.name();
                responseDto = new TicketVerifyResponse(false, resultStatus, "무효 처리된 티켓입니다.",
                        null, null, null,
                        verifier, deviceInfo, verifiedAt);

                ticketVerificationLogRepository.save(
                        new TicketVerificationLog(ticketCode, verifier, deviceInfo, resultStatus, verifiedAt)
                );

                try {
                    slackNotifier.send(String.format(
                            "\uD83D\uDEAB 취소 티켓 검증 시도 감지\n검증자: %s\n티켓코드: %s\nIP: %s",
                            verifier, ticketCode, deviceInfo
                    ));
                } catch (Exception ignored) {
                }

                cachedVerifyResponse(cacheKey, responseDto);

                return responseDto;
            }

            //2. Redis에서 티켓 유효성 확인
            String reservationSeatId = stringRedisTemplate.opsForValue().get(redisKey);

            if (reservationSeatId == null) {
                resultStatus = TicketStatus.EXPIRED.name();
                responseDto = new TicketVerifyResponse(false, resultStatus, "티켓이 만료되었습니다. 재발급이 필요합니다.",
                        null, null, null,
                        verifier, deviceInfo, verifiedAt);

                ticketVerificationLogRepository.save(
                        new TicketVerificationLog(ticketCode, verifier, deviceInfo, resultStatus, verifiedAt)
                );
                cachedVerifyResponse(cacheKey, responseDto);

                return responseDto;
            }

            //3. DB에서 티켓 조회
            Ticket ticket = ticketRepository.findByTicketCode(ticketCode)
                    .orElseThrow(() -> new CustomException(ErrorCode.TICKET_NOT_FOUND));

            Long dbReservationSeatId = ticket.getReservationSeat() != null ? ticket.getReservationSeat().getId() : null;

            if (dbReservationSeatId == null || !reservationSeatId.equals(dbReservationSeatId.toString())) {
                resultStatus = "TAMPERED";
                responseDto = new TicketVerifyResponse(
                        false, resultStatus, "티켓 정보가 유효하지 않습니다. 관리자에게 문의해주세요.",
                        null, null, null,
                        verifier, deviceInfo, verifiedAt
                );

                ticketVerificationLogRepository.save(
                        new TicketVerificationLog(ticketCode, verifier, deviceInfo, resultStatus, verifiedAt)
                );

                try {
                    slackNotifier.send(String.format(
                            "\u26A0\uFE0F 티켓 매핑 불일치 감지\n검증자: %s\n티켓코드: %s\nRedis reservationSeatId: %s\nDB reservationSeatId: %s\nIP: %s",
                            verifier, ticketCode, reservationSeatId, dbReservationSeatId, deviceInfo
                    ));
                } catch (Exception ignored) {
                }

                cachedVerifyResponse(cacheKey, responseDto);

                return responseDto;
            }

            String performanceTitle = ticket.getReservationSeat()
                    .getReservation().getPerformance().getTitle();
            String seatNo = ticket.getReservationSeat().getSeat().getSeatNo();
            LocalDateTime expiresAt = ticket.getExpiresAt();

            //4. 상태별 검표 처리
            if (ticket.getStatus() == TicketStatus.USED) {
                resultStatus = TicketStatus.USED.name();
                responseDto = new TicketVerifyResponse(false, resultStatus, "이미 사용된 티켓입니다.",
                        performanceTitle, seatNo, expiresAt,
                        verifier, deviceInfo, verifiedAt);
            } else if (ticket.getStatus() == TicketStatus.INVALIDATED) {
                resultStatus = TicketStatus.INVALIDATED.name();
                responseDto = new TicketVerifyResponse(false, resultStatus, "무효 처리된 티켓입니다.",
                        performanceTitle, seatNo, expiresAt,
                        verifier, deviceInfo, verifiedAt);
                try {
                    slackNotifier.send(String.format(
                            "\uD83D\uDEAB 무효 처리된 티켓 검증 시도 감지\n검증자: %s\n티켓코드: %s\nIP: %s",
                            verifier, ticketCode, deviceInfo
                    ));
                } catch (Exception ignored) {
                }
            } else if (ticket.getExpiresAt() != null && ticket.getExpiresAt().isBefore(LocalDateTime.now())) {
                resultStatus = TicketStatus.EXPIRED.name();
                responseDto = new TicketVerifyResponse(false, resultStatus, "티켓이 만료되었습니다.",
                        performanceTitle, seatNo, expiresAt,
                        verifier, deviceInfo, verifiedAt);
            } else {
                int updated = ticketRepository.markUsedIfIssued(ticketCode);

                if(updated == 0) {
                    resultStatus = TicketStatus.USED.name();
                    responseDto = new TicketVerifyResponse(false, resultStatus, "이미 사용된 티켓입니다.",
                            performanceTitle, seatNo, expiresAt,
                            verifier, deviceInfo, verifiedAt);
                } else {
                    resultStatus = TicketStatus.USED.name();
                    responseDto = new TicketVerifyResponse(true, resultStatus, "입장 완료",
                            performanceTitle, seatNo, expiresAt,
                            verifier, deviceInfo, verifiedAt);
                }
            }

            ticketVerificationLogRepository.save(
                    new TicketVerificationLog(ticketCode, verifier, deviceInfo, resultStatus, verifiedAt)
            );

            cachedVerifyResponse(cacheKey, responseDto);

            return responseDto;
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
}
