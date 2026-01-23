package com.byunsum.ticket_reservation.reservation.repository;

import com.byunsum.ticket_reservation.reservation.domain.pre.PreReservation;
import com.byunsum.ticket_reservation.reservation.domain.pre.PreReservationAssignment;
import com.byunsum.ticket_reservation.reservation.domain.pre.PreReservationAssignmentStatus;
import com.byunsum.ticket_reservation.seat.domain.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PreReservationAssignmentRepository extends JpaRepository<PreReservationAssignment, Long> {
    Optional<PreReservationAssignment> findByPreReservation(PreReservation preReservation);
    Optional<PreReservationAssignment> findBySeat(Seat seat);

    List<PreReservationAssignment> findByStatusAndExpiresAtBefore(PreReservationAssignmentStatus status, LocalDateTime now);
    List<PreReservationAssignment> findByPreReservationMemberId(Long memberId);
    List<PreReservationAssignment> findByPreReservationPerformanceId(Long performanceId);

    boolean existsBySeat(Seat seat);
}
