package com.byunsum.ticket_reservation.reservation.repository;

import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.reservation.domain.PreReservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PreReservationRepository extends JpaRepository<PreReservation, Long> {
    boolean existsByMember(Member member);
}
