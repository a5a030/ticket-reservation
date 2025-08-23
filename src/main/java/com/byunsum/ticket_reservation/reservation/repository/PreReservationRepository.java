package com.byunsum.ticket_reservation.reservation.repository;

import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.performance.domain.Performance;
import com.byunsum.ticket_reservation.reservation.domain.PreReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PreReservationRepository extends JpaRepository<PreReservation, Long> {
    boolean existsByMemberAndPerformance(Member member, Performance performance);

    List<PreReservation> findByPerformance(Performance performance);
    List<PreReservation> findByMember(Member member);

    Optional<PreReservation> findByMemberAndPerformance(Member member, Performance performance);
}
