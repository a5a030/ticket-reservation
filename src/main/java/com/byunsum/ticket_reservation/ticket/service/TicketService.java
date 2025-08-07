package com.byunsum.ticket_reservation.ticket.service;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.reservation.domain.Reservation;
import com.byunsum.ticket_reservation.reservation.repository.ReservationRepository;
import com.byunsum.ticket_reservation.ticket.domain.Ticket;
import com.byunsum.ticket_reservation.ticket.qr.QrCodeGenerator;
import com.byunsum.ticket_reservation.ticket.repository.TicketRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

public class TicketService {
    private final TicketRepository ticketRepository;
    private final ReservationRepository reservationRepository;
    private final QrCodeGenerator qrCodeGenerator;

    public TicketService(TicketRepository ticketRepository, ReservationRepository reservationRepository, QrCodeGenerator qrCodeGenerator) {
        this.ticketRepository = ticketRepository;
        this.reservationRepository = reservationRepository;
        this.qrCodeGenerator = qrCodeGenerator;
    }

    @Transactional
    public Ticket issueTicket(Long reservationId) {
        if(ticketRepository.existsByReservationId(reservationId)) {
            throw new CustomException(ErrorCode.TICKET_ALREADY_ISSUED);
        }

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        String qrCode = qrCodeGenerator.generate(reservation);

        Duration validDuration = Duration.ofHours(4);
        Ticket ticket = Ticket.create(reservation, qrCode, validDuration);

        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket getTicketByReservationId(Long reservationId) {
        return ticketRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.TICKET_NOT_FOUND));
    }
}
