package com.byunsum.ticket_reservation.ticket.service;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.reservation.domain.Reservation;
import com.byunsum.ticket_reservation.reservation.repository.ReservationRepository;
import com.byunsum.ticket_reservation.ticket.domain.Ticket;
import com.byunsum.ticket_reservation.ticket.domain.TicketStatus;
import com.byunsum.ticket_reservation.ticket.qr.QrCodeGenerator;
import com.byunsum.ticket_reservation.ticket.repository.TicketRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
}
