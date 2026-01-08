package com.byunsum.ticket_reservation.review.service;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.performance.domain.Performance;
import com.byunsum.ticket_reservation.performance.repository.PerformanceRepository;
import com.byunsum.ticket_reservation.review.domain.Review;
import com.byunsum.ticket_reservation.review.domain.SentimentType;
import com.byunsum.ticket_reservation.review.dto.KeywordSummary;
import com.byunsum.ticket_reservation.review.dto.ReviewDashboardResponse;
import com.byunsum.ticket_reservation.review.dto.ReviewResponse;
import com.byunsum.ticket_reservation.review.dto.ReviewStatisticsResponse;
import com.byunsum.ticket_reservation.review.repository.ReviewRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ReviewAdminService {
    private static final Logger log = LoggerFactory.getLogger(ReviewAdminService.class);
    private final ReviewRepository reviewRepository;
    private final PerformanceRepository performanceRepository;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final KeywordService keywordService;

    public ReviewAdminService(ReviewRepository reviewRepository, PerformanceRepository performanceRepository, StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper, KeywordService keywordService) {
        this.reviewRepository = reviewRepository;
        this.performanceRepository = performanceRepository;
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
        this.keywordService = keywordService;
    }

    @Transactional(readOnly = true)
    public ReviewDashboardResponse getDashboard(Long performanceId) {
        String cacheKey = "dashboard::"+performanceId;
        String cachedJson = stringRedisTemplate.opsForValue().get(cacheKey);

        if(cachedJson != null) {
            try {
                return objectMapper.readValue(cachedJson, ReviewDashboardResponse.class);
            } catch (JsonProcessingException e) {
                log.warn("Redis 캐시 역직렬화 실패: {}", e.getMessage());
            }
        }

        List<Review> reviews = reviewRepository.findByReservationPerformanceId(performanceId);

        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> new CustomException(ErrorCode.PERFORMANCE_NOT_FOUND));

        // 1. 통계 DTO 생성

        long positiveCount = reviews.stream().filter(r -> r.getSentiment() == SentimentType.POSITIVE).count();
        long negativeCount = reviews.stream().filter(r -> r.getSentiment() == SentimentType.NEGATIVE).count();
        long neutralCount  = reviews.stream().filter(r -> r.getSentiment() == SentimentType.NEUTRAL).count();


        double averageRating = reviews.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);

        ReviewStatisticsResponse statistics = new ReviewStatisticsResponse(
                performanceId,
                positiveCount,
                negativeCount,
                neutralCount,
                averageRating
        );

        // 2. 형태소 분석 기반 키워드 추출(요약 필드 기반)
        List<String> summaries = reviews.stream()
                .map(Review::getSummary)
                .filter(s -> s != null && !s.isBlank())
                .toList();

        List<KeywordSummary> keywords = keywordService.extractTopKeywordsWithCount(summaries, 2, 5);

        // 3. 샘플 리뷰(최신 3개)
        List<ReviewResponse> samples = reviews.stream()
                .sorted((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()))
                .limit(3)
                .map(r -> new ReviewResponse(
                        r.getId(),
                        r.getReservation().getId(),
                        r.getContent(),
                        r.getRating(),
                        r.getSentiment(),
                        r.getSentimentScore(),
                        r.getSummary(),
                        List.of(),
                        r.getCreatedAt()
                ))
                .toList();

        ReviewDashboardResponse response = new ReviewDashboardResponse(
                performanceId,
                performance.getTitle(),
                statistics,
                keywords,
                samples
        );

        try {
            stringRedisTemplate.opsForValue().set(
                    cacheKey,
                    objectMapper.writeValueAsString(response),
                    java.time.Duration.ofHours(1)
            );
        } catch (Exception e) {
            log.warn("Redis 캐시 저장 실패: {}", e.getMessage());
        }

        return response;
    }

    public void evictDashboardCache(Long performanceId) {
        String cacheKey = "dashboard::" + performanceId;
        stringRedisTemplate.delete(cacheKey);
    }
}
