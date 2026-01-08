package com.byunsum.ticket_reservation.review.service;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.reservation.domain.Reservation;
import com.byunsum.ticket_reservation.reservation.repository.ReservationRepository;
import com.byunsum.ticket_reservation.review.aop.InvalidateReviewCache;
import com.byunsum.ticket_reservation.review.domain.Review;
import com.byunsum.ticket_reservation.review.domain.SentimentType;
import com.byunsum.ticket_reservation.review.dto.ReviewRequest;
import com.byunsum.ticket_reservation.review.dto.ReviewResponse;
import com.byunsum.ticket_reservation.review.dto.ReviewStatisticsResponse;
import com.byunsum.ticket_reservation.review.external.*;
import com.byunsum.ticket_reservation.review.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Qualifier;
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
    private final KeywordClient keywordClient;

    public ReviewService(ReviewRepository reviewRepository, ReservationRepository reservationRepository, SentimentClient sentimentClient, SummaryClient summaryClient, ReviewAdminService reviewAdminService, @Qualifier("fallbackKeywordClient") KeywordClient keywordClient) {
        this.reviewRepository = reviewRepository;
        this.reservationRepository = reservationRepository;
        this.sentimentClient = sentimentClient;
        this.summaryClient = summaryClient;
        this.reviewAdminService = reviewAdminService;
        this.keywordClient = keywordClient;
    }

    @Transactional
    @InvalidateReviewCache
    public ReviewResponse createReview(ReviewRequest request, Member member) {
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        if(!reservation.getMember().getId().equals(member.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        // 1. 기본 리뷰 저장
        Review review = new Review(reservation, request.getContent(), request.getRating());
        Review saved = reviewRepository.save(review);

        // 2. 감정 분석
        SentimentResponse sentimentResponse = sentimentClient.analyzeSentiment(review.getContent());
        SentimentType sentiment = SentimentType.from(sentimentResponse.getSentiment());
        double score = sentimentResponse.getScore();

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
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

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
        String baseText = (review.getSummary() != null && !review.getSummary().isBlank())
                ? review.getSummary()
                : review.getContent();

        List<String> keywords = keywordClient.extractKeywords(baseText);

        return new ReviewResponse(
                review.getId(),
                review.getReservation().getId(),
                review.getContent(),
                review.getRating(),
                review.getSentiment(),
                review.getSentimentScore(),
                review.getSummary(),
                keywords,
                review.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public ReviewStatisticsResponse getReviewStatistics(Long performanceId) {
        List<Review> reviews = reviewRepository.findByReservationPerformanceId(performanceId);

        long positiveCount = reviews.stream().filter(r -> r.getSentiment() == SentimentType.POSITIVE).count();
        long negativeCount = reviews.stream().filter(r -> r.getSentiment() == SentimentType.NEGATIVE).count();
        long neutralCount  = reviews.stream().filter(r -> r.getSentiment() == SentimentType.NEUTRAL).count();


        double averageRating = reviews.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);

        return new ReviewStatisticsResponse(performanceId, positiveCount, negativeCount, neutralCount, averageRating);
    }

    @Transactional
    @InvalidateReviewCache
    public ReviewResponse updateReview(Long reviewId, ReviewRequest request, Member member) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        if(!review.getReservation().getMember().getId().equals(member.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        review.update(request.getContent(), request.getRating());

        SentimentResponse sentimentResponse = sentimentClient.analyzeSentiment(review.getContent());
        SummaryResponse summaryResponse = summaryClient.getSummary(review.getContent());

        SentimentType sentiment = SentimentType.from(sentimentResponse.getSentiment());
        double score = sentimentResponse.getScore();

        review.updateAI(summaryResponse.getSummary(), sentiment, score);

        return toResponse(review);
    }

    @Transactional
    @InvalidateReviewCache
    public void deleteReview(Long reviewId, Member member) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.REVIEW_NOT_FOUND));

        if(!review.getReservation().getMember().getId().equals(member.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_REVIEW_ACCESS);
        }

        reviewRepository.delete(review);
    }
}
