package com.byunsum.ticket_reservation.seat.repository;

import com.byunsum.ticket_reservation.performance.domain.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import com.byunsum.ticket_reservation.seat.domain.Seat;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    int countByPerformance(Performance performance);
}
