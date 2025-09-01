package com.byunsum.ticket_reservation.performance.repository;

import com.byunsum.ticket_reservation.performance.domain.Performance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long> {
    @Query("select p from Performance p order by p.startDate asc")
    Page<Performance> findAllOrderByStartDateAsc(Pageable pageable);

    @Query("select p from Performance p left join p.reservations r " + "group by p " + "order by count(r) desc")
    Page<Performance> findAllOrderByReservationsCountDesc(Pageable pageable);

    @Query
    Page<Performance> findAllByOrderByStartDateAscTimeAsc(Pageable pageable);

    @Query("select p.id from Performance p")
    List<Long> findAllIds();
}
