package com.byunsum.ticket_reservation.review.service;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.reservation.domain.Reservation;
import com.byunsum.ticket_reservation.reservation.repository.ReservationRepository;
import com.byunsum.ticket_reservation.review.domain.Review;
import com.byunsum.ticket_reservation.review.dto.ReviewRequest;
import com.byunsum.ticket_reservation.review.dto.ReviewResponse;
import com.byunsum.ticket_reservation.review.dto.ReviewStatisticsResponse;
import com.byunsum.ticket_reservation.review.external.SentimentClient;
import com.byunsum.ticket_reservation.review.external.SentimentResponse;
import com.byunsum.ticket_reservation.review.external.SummaryClient;
import com.byunsum.ticket_reservation.review.external.SummaryResponse;
import com.byunsum.ticket_reservation.review.repository.ReviewRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final SentimentClient sentimentClient;
    private final SummaryClient summaryClient;
    private final ReviewAdminService reviewAdminService;

    public ReviewService(ReviewRepository reviewRepository, ReservationRepository reservationRepository, SentimentClient sentimentClient, SummaryClient summaryClient, ReviewAdminService reviewAdminService) {
        this.reviewRepository = reviewRepository;
        this.reservationRepository = reservationRepository;
        this.sentimentClient = sentimentClient;
        this.summaryClient = summaryClient;
        this.reviewAdminService = reviewAdminService;
    }

    @Transactional
    public ReviewResponse createReview(ReviewRequest request, Member member) {
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        if(!reservation.getMember().getId().equals(member.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        // 1. 기본 리뷰 저장
        Review review = new Review(reservation, request.getContent(), request.getRating());
        Review saved = reviewRepository.save(review);

        Long performanceId = reservation.getPerformance().getId();
        reviewAdminService.evictDashboardCache(performanceId);

        // 2. 감정 분석
        SentimentResponse sentimentResponse = sentimentClient.analyzeSentiment(review.getContent());
        String sentiment = sentimentResponse.getSentiment();
        Double score = sentimentResponse.getScore();

        // 3. 요약 분석
        SummaryResponse summaryResponse = summaryClient.getSummary(review.getContent());
        String summary = summaryResponse.getSummary();

        // 4. 리뷰에 분석 결과 반영
        saved.updateAI(summary, sentiment, score);

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
    public List<ReviewResponse> getReviewsByPerformance(Long performanceId, Pageable pageable) {

        return reviewRepository.findByReservationPerformanceId(performanceId, pageable)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ReviewResponse toResponse(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getReservation().getId(),
                review.getContent(),
                review.getRating(),
                review.getSentiment(),
                review.getSentimentScore(),
                review.getSummary(),
                review.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public ReviewStatisticsResponse getReviewStatistics(Long performanceId) {
        List<Review> reviews = reviewRepository.findByReservationPerformanceId(performanceId);

        long positiveCount = reviews.stream().filter(r -> "POSITIVE".equalsIgnoreCase(r.getSentiment())).count();
        long negativeCount = reviews.stream().filter(r -> "NEGATIVE".equalsIgnoreCase(r.getSentiment())).count();
        long neutralCount = reviews.stream().filter(r -> "NEUTRAL".equalsIgnoreCase(r.getSentiment())).count();

        double averageRating = reviews.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);

        return new ReviewStatisticsResponse(performanceId, positiveCount, negativeCount, neutralCount, averageRating);
    }

    @Transactional
    public ReviewResponse updateReview(Long reviewId, ReviewRequest request, Member member) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        if(!review.getReservation().getMember().getId().equals(member.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        review.update(request.getContent(), request.getRating());

        SentimentResponse sentimentResponse = sentimentClient.analyzeSentiment(review.getContent());
        SummaryResponse summaryResponse = summaryClient.getSummary(review.getContent());

        review.updateAI(summaryResponse.getSummary(), sentimentResponse.getSentiment(), sentimentResponse.getScore());

        Long performanceId = review.getReservation().getPerformance().getId();
        reviewAdminService.evictDashboardCache(performanceId);

        return toResponse(review);
    }

    @Transactional
    public void deleteReview(Long reviewId, Member member) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        if(!review.getReservation().getMember().getId().equals(member.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_REVIEW_ACCESS);
        }

        Long performanceId = review.getReservation().getPerformance().getId();
        reviewRepository.delete(review);

        reviewAdminService.evictDashboardCache(performanceId);
    }
}
