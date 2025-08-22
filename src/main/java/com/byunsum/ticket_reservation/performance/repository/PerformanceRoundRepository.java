package com.byunsum.ticket_reservation.performance.repository;

import com.byunsum.ticket_reservation.performance.domain.Performance;
import com.byunsum.ticket_reservation.performance.domain.PerformanceRound;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PerformanceRoundRepository extends JpaRepository<PerformanceRound, Long> {
    List<PerformanceRound> findByPerformance(Performance performance);
}
