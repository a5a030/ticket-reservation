package com.byunsum.ticket_reservation.seat.repository;

import com.byunsum.ticket_reservation.performance.domain.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import com.byunsum.ticket_reservation.seat.domain.Seat;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    interface PerformanceSeatIdRow {
        Long getPerformanceId();
        Long getSeatId();
    }

    @Query("SELECT COUNT(s) FROM Seat s WHERE s.performanceRound.performance = :performance")
    long countByPerformance(@Param("performance") Performance performance);

    @Query("""
    select s.id
    from Seat s
    where s.performanceRound.performance.id = :performanceId
    """)
    List<Long> findSeatIdsByPerformanceId(@Param("performanceId") Long performanceId);

    @Query("""
        select
          s.performanceRound.performance.id as performanceId,
          s.id as seatId
        from Seat s
        """)
    List<PerformanceSeatIdRow> findPerformanceIdAndSeatId();

}
