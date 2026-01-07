package com.byunsum.ticket_reservation.performance.repository;

import com.byunsum.ticket_reservation.performance.domain.PerformanceRound;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface PerformanceRoundRepository extends JpaRepository<PerformanceRound, Long> {
    List<PerformanceRound> findByPerformanceIdOrderByStartDateTimeAsc(Long performanceId);
    List<PerformanceRound> findByEntryDateTimeBetween(LocalDateTime start, LocalDateTime end);
    List<PerformanceRound> findByStartDateTimeBetween(LocalDateTime start, LocalDateTime end);
}
