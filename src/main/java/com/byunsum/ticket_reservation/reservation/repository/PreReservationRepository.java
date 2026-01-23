package com.byunsum.ticket_reservation.reservation.repository;

import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.performance.domain.PerformanceRound;
import com.byunsum.ticket_reservation.reservation.domain.PreReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PreReservationRepository extends JpaRepository<PreReservation, Long> {
    boolean existsByMemberAndPerformanceRound(Member member, PerformanceRound performanceRound);

    List<PreReservation> findByPerformanceRound(PerformanceRound performanceRound);
    List<PreReservation> findByMember(Member member);

    Optional<PreReservation> findByMemberAndPerformanceRound(Member member, PerformanceRound performanceRound);
}
