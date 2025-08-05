package com.byunsum.ticket_reservation.review.repository;

import com.byunsum.ticket_reservation.review.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByReservationId(Long reservationId);
    boolean existsByReservationId(Long reservationId);

    List<Review> findByReservationMemberId(Long memberId);
    List<Review> findByReservationPerformanceId(Long performanceId);

    Page<Review> findByReservationPerformanceId(Long performanceId, Pageable pageable);
}
