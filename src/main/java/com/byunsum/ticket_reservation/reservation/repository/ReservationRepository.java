package com.byunsum.ticket_reservation.reservation.repository;

import com.byunsum.ticket_reservation.performance.domain.Performance;
import com.byunsum.ticket_reservation.reservation.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByReservationCode(String reservationCode);

    Long id(Long id);

    List<Reservation> findByMemberIdOrderByCreatedAtDesc(Long memberId);

    @Query("select r from Reservation r where r.member.id = :memberId order by r.performance.startDate asc")
    List<Reservation> findByMemberIdOrderByPerformanceStartDateAsc(@Param("memberId") Long memberId);

    @Query("select r.performance from Reservation r group by r.performance order by count(r) desc")
    List<Performance> findPopularPerformances();
}
