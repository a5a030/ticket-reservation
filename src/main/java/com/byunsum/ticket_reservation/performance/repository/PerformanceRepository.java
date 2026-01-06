package com.byunsum.ticket_reservation.performance.repository;

import com.byunsum.ticket_reservation.performance.domain.Performance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long> {
    @Query("select p.id from Performance p")
    List<Long> findAllIds();
}
