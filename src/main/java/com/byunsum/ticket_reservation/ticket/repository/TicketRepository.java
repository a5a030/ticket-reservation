package com.byunsum.ticket_reservation.ticket.repository;

import com.byunsum.ticket_reservation.ticket.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket,Long> {
    Optional<Ticket> findByReservationId(Long reservationId);
    boolean existsByReservationId(Long reservationId);
}
