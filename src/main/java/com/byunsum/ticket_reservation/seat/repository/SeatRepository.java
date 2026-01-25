package com.byunsum.ticket_reservation.seat.repository;

import com.byunsum.ticket_reservation.performance.domain.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import com.byunsum.ticket_reservation.seat.domain.Seat;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    @Query("SELECT COUNT(s) FROM Seat s WHERE s.performanceRound.performance = :performance")
    long countByPerformance(@Param("performance") Performance performance);

}
