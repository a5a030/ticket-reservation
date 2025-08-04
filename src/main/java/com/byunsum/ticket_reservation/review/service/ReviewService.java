package com.byunsum.ticket_reservation.review.service;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.reservation.domain.Reservation;
import com.byunsum.ticket_reservation.reservation.repository.ReservationRepository;
import com.byunsum.ticket_reservation.review.domain.Review;
import com.byunsum.ticket_reservation.review.dto.ReviewRequest;
import com.byunsum.ticket_reservation.review.dto.ReviewResponse;
import com.byunsum.ticket_reservation.review.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;

    public ReviewService(ReviewRepository reviewRepository, ReservationRepository reservationRepository) {
        this.reviewRepository = reviewRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public ReviewResponse createReview(ReviewRequest request, Member member) {
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        if(!reservation.getMember().getId().equals(member.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Review review = new Review(reservation, request.getContent(), request.getRating());
        Review saved = reviewRepository.save(review);

        return toResponse(saved);
    }


    @Transactional(readOnly = true)
    public ReviewResponse getReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        return toResponse(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByMember(Long memberId) {
        List<Review> reviews = reviewRepository.findByReservationMemberId(memberId);

        return reviews.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByPerformance(Long performanceId) {
        List<Review> reviews = reviewRepository.findByReservationPerformanceId(performanceId);

        return reviews.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ReviewResponse toResponse(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getReservation().getId(),
                review.getContent(),
                review.getRating(),
                review.getCreatedAt()
        );
    }
}
