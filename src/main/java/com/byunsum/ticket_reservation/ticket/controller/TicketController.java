package com.byunsum.ticket_reservation.ticket.controller;

import com.byunsum.ticket_reservation.ticket.domain.Ticket;
import com.byunsum.ticket_reservation.ticket.service.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

public class TicketController {
    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping("/{reservationId}")
    public ResponseEntity<Ticket> issueTicket(@PathVariable Long reservationId) {
        Ticket ticket = ticketService.issueTicket(reservationId);

        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/{reservationId}")
    public ResponseEntity<Ticket> getTicket(@PathVariable Long reservationId) {
        Ticket ticket = ticketService.getTicketByReservationId(reservationId);

        return ResponseEntity.ok(ticket);
    }
}
