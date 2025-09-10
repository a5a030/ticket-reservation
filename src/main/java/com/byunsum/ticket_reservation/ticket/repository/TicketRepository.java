package com.byunsum.ticket_reservation.ticket.repository;

import com.byunsum.ticket_reservation.ticket.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket,Long> {
    Optional<Ticket> findByReservationSeatId(Long reservationSeatId);
    boolean existsByReservationSeatId(Long reservationSeatId);

    Optional<Ticket> findByTicketCode(String ticketCode);

    @Query("select t from Ticket t " +
            "where t.expiresAt < :now " +
            "and  t.status = 'ISSUED'")
    List<Ticket> findExpiredTickets(@Param("now") LocalDateTime now);
}
