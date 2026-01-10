package com.byunsum.ticket_reservation.ticket.repository;

import com.byunsum.ticket_reservation.performance.domain.Performance;
import com.byunsum.ticket_reservation.performance.domain.PerformanceRound;
import com.byunsum.ticket_reservation.ticket.domain.Ticket;
import com.byunsum.ticket_reservation.ticket.domain.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket,Long> {
    Optional<Ticket> findFirstByReservationSeatIdAndStatusOrderByIssuedAtDesc(Long reservationSeatId, TicketStatus status);
    Optional<Ticket> findFirstByReservationSeatIdOrderByIssuedAtDesc(Long reservationSeatId);
    boolean existsByReservationSeatIdAndStatus(Long reservationSeatId, TicketStatus status);

    Optional<Ticket> findByTicketCode(String ticketCode);

    @Query("select t from Ticket t " +
            "where t.expiresAt < :now " +
            "and t.status = 'ISSUED' " +
            "order by t.expiresAt asc ")
    List<Ticket> findExpiredTickets(@Param("now") LocalDateTime now);

    @Query("select count(t) from Ticket t " +
            "join t.reservationSeat rs "+
            "join rs.reservation r " +
            "where r.performance = :performance")
    long countByPerformance(@Param("performance") Performance performance);

    @Query("select count(t) from Ticket t " +
            "join t.reservationSeat rs "+
            "join rs.reservation r " +
            "where r.performance = :performance and t.status = 'USED'")
    long countUsedByPerformance(@Param("performance") Performance performance);


    @Query("select count(t) from Ticket t " +
            "join t.reservationSeat rs " +
            "join rs.seat s " +
            "where s.performanceRound = :round")
    long countByPerformanceRound(@Param("round") PerformanceRound round);

    @Query("select count(t) from Ticket t " +
            "join t.reservationSeat rs " +
            "join rs.seat s " +
            "where s.performanceRound = :round and t.status = 'USED'")
    long countUsedByPerformanceRound(@Param("round") PerformanceRound round);
}
