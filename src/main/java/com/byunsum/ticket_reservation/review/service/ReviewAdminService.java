package com.byunsum.ticket_reservation.review.service;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.performance.domain.Performance;
import com.byunsum.ticket_reservation.performance.repository.PerformanceRepository;
import com.byunsum.ticket_reservation.review.domain.Review;
import com.byunsum.ticket_reservation.review.dto.KeywordSummary;
import com.byunsum.ticket_reservation.review.dto.ReviewDashboardResponse;
import com.byunsum.ticket_reservation.review.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReviewAdminService {
    private final ReviewRepository reviewRepository;
    private final PerformanceRepository performanceRepository;

    public ReviewAdminService(ReviewRepository reviewRepository, PerformanceRepository performanceRepository) {
        this.reviewRepository = reviewRepository;
        this.performanceRepository = performanceRepository;
    }

    @Transactional(readOnly = true)
    public ReviewDashboardResponse getDashboard(Long performanceId) {
        List<Review> reviews = reviewRepository.findByReservationPerformanceId(performanceId);

        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> new CustomException(ErrorCode.PERFORMANCE_NOT_FOUND));

        int totalCount = reviews.size();

        long positiveCount = reviews.stream().filter(r -> "POSITIVE".equalsIgnoreCase(r.getSentiment())).count();
        long negativeCount = reviews.stream().filter(r -> "NEGATIVE".equalsIgnoreCase(r.getSentiment())).count();
        long neutralCount = reviews.stream().filter(r -> "NEUTRAL".equalsIgnoreCase(r.getSentiment())).count();

        double averageRating = reviews.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);

        double positiveRatio = totalCount > 0 ? (positiveCount * 100.0 / totalCount) : 0.0;

        // 감정별 예시 추출
        Map<String, List<String>> examples = new HashMap<>();
        examples.put("positive", extraExamplesBySentiment(reviews, "POSITIVE"));
        examples.put("negative", extraExamplesBySentiment(reviews, "NEGATIVE"));
        examples.put("neutral", extraExamplesBySentiment(reviews, "NEUTRAL"));

        // 키워드 요약(summary 필드 기준으로 명사 뽑기)
        List<KeywordSummary> keywordSummaries = extractTopKeywords(reviews, 5);

        return new ReviewDashboardResponse(
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
    }

    private List<String> extraExamplesBySentiment(List<Review> reviews, String sentiment) {
        return reviews.stream()
                .filter(r -> sentiment.equalsIgnoreCase(r.getSentiment()))
                .map(Review::getContent)
                .limit(3)
                .collect(Collectors.toList());
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

        List<String> positive = reviews.stream()
                .filter(r -> "긍정".equals(r.getSentiment()))
                .map(Review::getContent)
                .filter(c -> c != null && !c.isBlank())
                .limit(perSentimentLimit)
                .collect(Collectors.toList());

        List<String> negative = reviews.stream()
                .filter(r -> "부정".equals(r.getSentiment()))
                .map(Review::getContent)
                .filter(c -> c != null && !c.isBlank())
                .limit(perSentimentLimit)
                .collect(Collectors.toList());

        List<String> neutral = reviews.stream()
                .filter(r -> "중립".equals(r.getSentiment()))
                .map(Review::getContent)
                .filter(c -> c != null && !c.isBlank())
                .limit(perSentimentLimit)
                .collect(Collectors.toList());

        exampleMap.put("긍정", positive);
        exampleMap.put("부정", negative);
        exampleMap.put("중립", neutral);

        return exampleMap;
    }
}
