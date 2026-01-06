package com.byunsum.ticket_reservation.performance.repository;

import com.byunsum.ticket_reservation.performance.domain.Performance;
import com.byunsum.ticket_reservation.performance.domain.PerformanceRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PerformanceRoundRepository extends JpaRepository<PerformanceRound, Long> {List<PerformanceRound> findByEntryDateTimeBetween(LocalDateTime start, LocalDateTime end);

    List<PerformanceRound> findByPerformanceIdOrderByStartDateTimeAsc(Long performanceId);
}
