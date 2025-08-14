package com.byunsum.ticket_reservation.ticket.service;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.reservation.domain.Reservation;
import com.byunsum.ticket_reservation.reservation.repository.ReservationRepository;
import com.byunsum.ticket_reservation.ticket.domain.Ticket;
import com.byunsum.ticket_reservation.ticket.domain.TicketStatus;
import com.byunsum.ticket_reservation.ticket.dto.TicketVerifyResponse;
import com.byunsum.ticket_reservation.ticket.qr.QrCodeGenerator;
import com.byunsum.ticket_reservation.ticket.repository.TicketRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
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

    public TicketService(TicketRepository ticketRepository, ReservationRepository reservationRepository, QrCodeGenerator qrCodeGenerator, StringRedisTemplate stringRedisTemplate) {
        this.ticketRepository = ticketRepository;
        this.reservationRepository = reservationRepository;
        this.qrCodeGenerator = qrCodeGenerator;
        this.stringRedisTemplate = stringRedisTemplate;
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

        String newCode = UUID.randomUUID().toString();
        String newQrImage =  qrCodeGenerator.generate(newCode);

        Reservation reservation = ticket.getReservation();
        reservation.setTicketCode(newCode);
        reservationRepository.save(reservation);

        ticket.updateQrCode(
                newCode,
                newQrImage,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(30)
        );

        stringRedisTemplate.opsForValue().set(
                REDIS_KEY_PREFIX + newCode,
                reservationId.toString(),
                30, TimeUnit.MINUTES
        );

        return ticket;
    }

    @Transactional
    public TicketVerifyResponse verifyTicket(String ticketCode) {
        String redisKey = REDIS_KEY_PREFIX + ticketCode;
        String reservationId = stringRedisTemplate.opsForValue().get(redisKey);

        if(reservationId == null) {
            return new TicketVerifyResponse(false, "EXPIRED", "티켓이 만료되었습니다. 재발급이 필요합니다.");
        }

        Ticket ticket = ticketRepository.findByTicketCode(ticketCode)
                .orElseThrow(() -> new CustomException(ErrorCode.TICKET_NOT_FOUND));

        if(ticket.getStatus() == TicketStatus.USED) {
            return new TicketVerifyResponse(false, "ALREADY_USED", "이미 사용된 티켓입니다.");
        }

        LocalDateTime now =  LocalDateTime.now();
        if(ticket.getExpiresAt().isBefore(now)) {
            return new TicketVerifyResponse(false, "EXPIRED", "티켓이 만료되었습니다.");
        }

        ticket.setStatus(TicketStatus.USED);
        ticketRepository.save(ticket);

        return new TicketVerifyResponse(true, "USED", "입장 완료");
    }
}
