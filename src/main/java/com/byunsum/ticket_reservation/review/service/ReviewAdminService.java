package com.byunsum.ticket_reservation.review.service;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.performance.domain.Performance;
import com.byunsum.ticket_reservation.performance.repository.PerformanceRepository;
import com.byunsum.ticket_reservation.review.domain.Review;
import com.byunsum.ticket_reservation.review.domain.SentimentType;
import com.byunsum.ticket_reservation.review.dto.KeywordSummary;
import com.byunsum.ticket_reservation.review.dto.ReviewDashboardResponse;
import com.byunsum.ticket_reservation.review.repository.ReviewRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReviewAdminService {
    private static final Logger log = LoggerFactory.getLogger(ReviewAdminService.class);
    private final ReviewRepository reviewRepository;
    private final PerformanceRepository performanceRepository;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public ReviewAdminService(ReviewRepository reviewRepository, PerformanceRepository performanceRepository, StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper) {
        this.reviewRepository = reviewRepository;
        this.performanceRepository = performanceRepository;
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
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

        int totalCount = reviews.size();

        long positiveCount = reviews.stream().filter(r -> SentimentType.from(r.getSentiment()) == SentimentType.POSITIVE).count();
        long negativeCount = reviews.stream().filter(r -> SentimentType.from(r.getSentiment()) == SentimentType.NEGATIVE).count();
        long neutralCount = reviews.stream().filter(r -> SentimentType.from(r.getSentiment()) == SentimentType.NEUTRAL).count();

        double averageRating = reviews.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);

        double positiveRatio = totalCount > 0 ? (positiveCount * 100.0 / totalCount) : 0.0;

        // 감정별 예시 추출
        Map<String, List<String>> examples = extractRepresentativeExamples(reviews, 3);

        // 키워드 요약(summary 필드 기준으로 명사 뽑기)
        List<KeywordSummary> keywordSummaries = extractTopKeywords(reviews, 5);

        ReviewDashboardResponse response = new ReviewDashboardResponse(
                performanceId,
                performance.getTitle(),
                totalCount,
                (int) positiveCount,
                (int) negativeCount,
                (int) neutralCount,
                positiveRatio,
                averageRating,
                keywordSummaries,
                examples
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

    private List<KeywordSummary> extractTopKeywords(List<Review> reviews, int limit) {
        Map<String, Integer> keywordCount = new HashMap<>();

        for(Review review : reviews) {
            if(review.getSummary() != null) {
                String[] words = review.getSummary().split("\\s+");

                for(String word : words) {
                    if(word.length() >= 2) {
                        keywordCount.put(word, keywordCount.getOrDefault(word, 0) + 1);
                    }
                }
            }
        }

        return keywordCount.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limit)
                .map(e -> new KeywordSummary(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    public Map<String, List<String>> extractRepresentativeExamples(List<Review> reviews, int perSentimentLimit) {
        Map<String, List<String>> exampleMap = new HashMap<>();

        for(SentimentType sentiment : SentimentType.values()) {
            List<String> examples = reviews.stream()
                    .filter(r -> {
                        try {
                            return SentimentType.from(r.getSentiment()) == sentiment;
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .map(Review::getContent)
                    .filter(c -> c != null && !c.isBlank())
                    .limit(perSentimentLimit)
                    .collect(Collectors.toList());

            exampleMap.put(sentiment.getDisplayName(), examples);
        }

        return exampleMap;
    }
}
