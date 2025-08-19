package com.byunsum.ticket_reservation.ticket.service;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.global.monitoring.SlackNotifier;
import com.byunsum.ticket_reservation.reservation.domain.Reservation;
import com.byunsum.ticket_reservation.reservation.repository.ReservationRepository;
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
    public Ticket issueTicket(Long reservationId) {
        if(ticketRepository.existsByReservationId(reservationId)) {
            throw new CustomException(ErrorCode.TICKET_ALREADY_ISSUED);
        }

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        String ticketCode = UUID.randomUUID().toString();

        reservation.setTicketCode(ticketCode);
        reservationRepository.save(reservation);

        String qrImageUrl = qrCodeGenerator.generate(ticketCode);

        Ticket ticket = Ticket.create(reservation, qrImageUrl, TICKET_VALID_DURATION);
        Ticket saved = ticketRepository.save(ticket);

        stringRedisTemplate.opsForValue().set(
                REDIS_KEY_PREFIX + ticketCode,
                reservationId.toString(),
                TICKET_VALID_DURATION.toMinutes(),
                TimeUnit.MINUTES
        );

        return saved;
    }

    @Transactional
    public Ticket getTicketByReservationId(Long reservationId) {
        return ticketRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.TICKET_NOT_FOUND));
    }

    @Transactional
    public Ticket refreshQrCode(Long reservationId) {
        Ticket ticket = ticketRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.TICKET_NOT_FOUND));

        LocalDateTime performanceStart = ticket.getReservation().getPerformance().getStartTime();

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
                reservationId.toString(),
                30, TimeUnit.MINUTES
        );

        //보안 강화 위해 기존 코드 블랙리스트 등록
        stringRedisTemplate.opsForValue().set(
                "blacklist:ticket:" + oldCode,
                "true",
                Duration.ofHours(48)
        );

        String loginId = ticket.getReservation().getMember().getLoginId();
        String username = ticket.getReservation().getMember().getUsername();

        ticketReissueLogRepository.save(
                new TicketReissueLog(oldCode, newCode, loginId, username, LocalDateTime.now())
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
            resultStatus = "CANCELLED";
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
            resultStatus = "EXPIRED";
            responseDto = new TicketVerifyResponse(false, resultStatus, "티켓이 만료되었습니다. 재발급이 필요합니다.",
                    null, null, null,
                    verifier, deviceInfo, verifiedAt);
        } else {
            Ticket ticket = ticketRepository.findByTicketCode(ticketCode)
                    .orElseThrow(() -> new CustomException(ErrorCode.TICKET_NOT_FOUND));

            String performanceTitle = ticket.getReservation().getPerformance().getTitle();
            String seatNo = ticket.getReservation().getSeat().getSeatNo();
            LocalDateTime expiresAt = ticket.getExpiresAt();

            if(ticket.getStatus() == TicketStatus.USED) {
                resultStatus = "ALREADY_USED";
                responseDto = new TicketVerifyResponse(false, resultStatus, "이미 사용된 티켓입니다.",
                        performanceTitle, seatNo, expiresAt,
                        verifier, deviceInfo, verifiedAt);
            } else if(ticket.getStatus() == TicketStatus.CANCELLED) {
                slackNotifier.send(String.format(
                        "\uD83D\uDEAB 취소 티켓 검증 시도 감지\n검증자: %s\n티켓코드: %s\nIP: %s",
                        verifier, ticketCode, deviceInfo
                ));

                resultStatus = "CANCELLED";
                responseDto = new TicketVerifyResponse(false, resultStatus, "취소된 티켓입니다.",
                        performanceTitle, seatNo, expiresAt,
                        verifier, deviceInfo, verifiedAt);
            } else if(ticket.getExpiresAt().isBefore(LocalDateTime.now())) {
                resultStatus = "EXPIRED";
                responseDto = new TicketVerifyResponse(false, "EXPIRED", "티켓이 만료되었습니다.",
                        performanceTitle, seatNo, expiresAt,
                        verifier, deviceInfo, verifiedAt);
            } else {
                ticket.setStatus(TicketStatus.USED);
                ticketRepository.save(ticket);

                resultStatus = "USED";
                responseDto = new TicketVerifyResponse(true, "USED", "입장 완료",
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

        //블랙리스트 키 등록
        stringRedisTemplate.opsForValue().set(
                "blacklist:ticket:" + ticketCode,
                "true",
                Duration.ofHours(48)
        );
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
