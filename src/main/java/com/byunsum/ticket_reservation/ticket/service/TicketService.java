package com.byunsum.ticket_reservation.ticket.service;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.global.monitoring.SlackNotifier;
import com.byunsum.ticket_reservation.reservation.domain.Reservation;
import com.byunsum.ticket_reservation.reservation.domain.ReservationSeat;
import com.byunsum.ticket_reservation.reservation.repository.ReservationRepository;
import com.byunsum.ticket_reservation.reservation.repository.ReservationSeatRepository;
import com.byunsum.ticket_reservation.ticket.domain.Ticket;
import com.byunsum.ticket_reservation.ticket.domain.TicketReissueLog;
import com.byunsum.ticket_reservation.ticket.domain.TicketStatus;
import com.byunsum.ticket_reservation.ticket.domain.TicketVerificationLog;
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
            if(ticketRepository.existsByReservationSeatId(rs.getId())) {
                throw new CustomException(ErrorCode.TICKET_ALREADY_ISSUED);
            }

            String ticketCode = UUID.randomUUID().toString();
            String qrImageUrl = qrCodeGenerator.generate(ticketCode);

            Ticket ticket = Ticket.create(rs, qrImageUrl, TICKET_VALID_DURATION);
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
            ticketRepository.findByReservationSeatId(rs.getId()).ifPresent(tickets::add);
        }

        return tickets;
    }

    @Transactional
    public Ticket refreshQrCode(Long reservationSeatId) {
        Ticket ticket = ticketRepository.findByReservationSeatId(reservationSeatId)
                .orElseThrow(() -> new CustomException(ErrorCode.TICKET_NOT_FOUND));

        LocalDateTime performanceStart = ticket.getReservationSeat()
                .getReservation().getPerformance().getStartTime();

        if(LocalDateTime.now().isBefore(performanceStart.minusHours(3))) {
            throw new CustomException(ErrorCode.QR_NOT_YET_AVAILABLE);
        }

        String oldCode = ticket.getTicketCode();
        String newCode = UUID.randomUUID().toString();
        String newQrImage =  qrCodeGenerator.generate(newCode);

        ticket.refresh(newCode, newQrImage, Duration.ofMinutes(30));
        ticketRepository.save(ticket);

        stringRedisTemplate.opsForValue().set(
                REDIS_KEY_PREFIX + newCode,
                reservationSeatId.toString(),
                30, TimeUnit.MINUTES
        );

        LocalDateTime performanceEnd = ticket.getReservationSeat()
                .getReservation().getPerformance().getEndDateTime();

        if(performanceEnd == null) {
            throw new CustomException(ErrorCode.PERFORMANCE_NOT_FOUND);
        }

        long expiresAtMillis = performanceEnd.plusDays(1)
                .atZone(ZoneId.systemDefault())
                .toInstant().toEpochMilli();

        String blacklistKey = "blacklist:ticket:" + oldCode;
        stringRedisTemplate.opsForValue().set(blacklistKey, "true");
        stringRedisTemplate.expireAt(blacklistKey, new Date(expiresAtMillis));

        String loginId = ticket.getReservationSeat()
                .getReservation().getMember().getLoginId();
        String username = ticket.getReservationSeat()
                .getReservation().getMember().getUsername();

        ticketReissueLogRepository.save(
                new TicketReissueLog(ticket.getReservationSeat(), oldCode, newCode, loginId, username, LocalDateTime.now())
        );

        return ticket;
    }

    @Transactional
    public TicketVerifyResponse verifyTicket(String ticketCode, HttpServletRequest request) {
        String cacheKey = "verifyTicket:" + ticketCode;

        String cached = stringRedisTemplate.opsForValue().get(cacheKey);

        if(cached != null) {
            try {
                return objectMapper.readValue(cached, TicketVerifyResponse.class);
            } catch (Exception e) {}
        }
        String redisKey = REDIS_KEY_PREFIX + ticketCode;

        String verifier = SecurityContextHolder.getContext().getAuthentication().getName();
        String deviceInfo = clientIp(request);
        String resultStatus;
        TicketVerifyResponse responseDto;

        LocalDateTime verifiedAt = LocalDateTime.now();

        //1. 블랙리스트 조회
        String blacklistKey = "blacklist:ticket:" + ticketCode;

        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(blacklistKey))) {
            resultStatus = TicketStatus.CANCELLED.name();
            responseDto = new TicketVerifyResponse(false, resultStatus, "취소된 티켓입니다.",
                    null, null, null,
                    verifier, deviceInfo, verifiedAt);
            ticketVerificationLogRepository.save(
                    new TicketVerificationLog(ticketCode, verifier, deviceInfo, resultStatus, verifiedAt)
            );

            slackNotifier.send(String.format(
                    "\uD83D\uDEAB 취소 티켓 검증 시도 감지\n검증자: %s\n티켓코드: %s\nIP: %s",
                    verifier, ticketCode, deviceInfo
            ));

            cachedVerifyResponse(cacheKey, responseDto);

            return responseDto;
        }

        String reservationId = stringRedisTemplate.opsForValue().get(redisKey);

        if(reservationId == null) {
            resultStatus = TicketStatus.EXPIRED.name();
            responseDto = new TicketVerifyResponse(false, resultStatus, "티켓이 만료되었습니다. 재발급이 필요합니다.",
                    null, null, null,
                    verifier, deviceInfo, verifiedAt);
        } else {
            Ticket ticket = ticketRepository.findByTicketCode(ticketCode)
                    .orElseThrow(() -> new CustomException(ErrorCode.TICKET_NOT_FOUND));

            String performanceTitle = ticket.getReservationSeat()
                    .getReservation().getPerformance().getTitle();
            String seatNo = ticket.getReservationSeat().getSeat().getSeatNo();
            LocalDateTime expiresAt = ticket.getExpiresAt();

            if(ticket.getStatus() == TicketStatus.USED) {
                resultStatus = TicketStatus.USED.name();
                responseDto = new TicketVerifyResponse(false, resultStatus, "이미 사용된 티켓입니다.",
                        performanceTitle, seatNo, expiresAt,
                        verifier, deviceInfo, verifiedAt);
            } else if(ticket.getStatus() == TicketStatus.CANCELLED) {
                slackNotifier.send(String.format(
                        "\uD83D\uDEAB 취소 티켓 검증 시도 감지\n검증자: %s\n티켓코드: %s\nIP: %s",
                        verifier, ticketCode, deviceInfo
                ));

                resultStatus = TicketStatus.CANCELLED.name();
                responseDto = new TicketVerifyResponse(false, resultStatus, "취소된 티켓입니다.",
                        performanceTitle, seatNo, expiresAt,
                        verifier, deviceInfo, verifiedAt);
            } else if(ticket.getExpiresAt().isBefore(LocalDateTime.now())) {
                resultStatus = TicketStatus.EXPIRED.name();
                responseDto = new TicketVerifyResponse(false, resultStatus, "티켓이 만료되었습니다.",
                        performanceTitle, seatNo, expiresAt,
                        verifier, deviceInfo, verifiedAt);
            } else {
                ticket.setStatus(TicketStatus.USED);
                ticketRepository.save(ticket);

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

        ticket.setStatus(TicketStatus.CANCELLED);
        ticketRepository.save(ticket);

        //Redis TTl 삭제
        stringRedisTemplate.delete(REDIS_KEY_PREFIX + ticketCode);

        LocalDateTime performanceEnd = ticket.getReservationSeat()
                .getReservation().getPerformance().getEndDateTime();

        if(performanceEnd == null) {
            throw new CustomException(ErrorCode.PERFORMANCE_NOT_FOUND);
        }

        long expiresAtMillis = performanceEnd.plusDays(1)
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
}
