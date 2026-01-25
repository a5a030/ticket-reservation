package com.byunsum.ticket_reservation.performance.repository;

import com.byunsum.ticket_reservation.performance.domain.Performance;
import com.byunsum.ticket_reservation.reservation.domain.sale.SalePhase;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long> {
    @Query("select p.id from Performance p")
    List<Long> findAllIds();

    @Query("""
    select p.id from Performance p
    where
      (
        p.salePhase = :preSale
        and p.preReservationOpenDateTime <= current_timestamp
      )
      or
      (
        p.salePhase = :generalSale
        and p.generalReservationOpenDateTime <= current_timestamp
      )
""")
    List<Long> findQueueOpenPerformanceIds(
            @Param("preSale") SalePhase preSale,
            @Param("generalSale") SalePhase generalSale
    );
}
