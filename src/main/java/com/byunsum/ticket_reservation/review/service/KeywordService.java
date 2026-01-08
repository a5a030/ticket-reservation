package com.byunsum.ticket_reservation.review.service;

import com.byunsum.ticket_reservation.review.domain.Review;
import com.byunsum.ticket_reservation.review.dto.KeywordSummary;
import com.byunsum.ticket_reservation.review.repository.ReviewRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openkoreantext.processor.KoreanTokenJava;
import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import org.openkoreantext.processor.tokenizer.KoreanTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import scala.collection.Seq;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class KeywordService {
    private static final Logger log = LoggerFactory.getLogger(KeywordService.class);

    private final ReviewRepository reviewRepository;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    private static final String CACHE_PREFIX = "review:keywords:v1";

    public KeywordService(ReviewRepository reviewRepository, StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper) {
        this.reviewRepository = reviewRepository;
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
    }

    private Map<String, Integer> buildFrequencyMap(List<String> reviews, int minLength) {
        Map<String, Integer> freqMap = new HashMap<>();

        for (String review : reviews) {
            if(review==null || review.trim().isEmpty()) continue;

            CharSequence normalized = OpenKoreanTextProcessorJava.normalize(review);
            Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(normalized);
            List<KoreanTokenJava> tokenList = OpenKoreanTextProcessorJava.tokensToJavaKoreanTokenList(tokens);

            tokenList.stream()
                    .filter(token -> token.getPos().toString().contains("Noun"))
                    .map(KoreanTokenJava::getText)
                    .filter(word -> word.length() >= minLength)
                    .forEach(word -> freqMap.put(word, freqMap.getOrDefault(word, 0) + 1));
        }

        return freqMap;
    }

    public List<String> extractTopNouns(List<String> reviews, int minLength, int keywordLimit) {
        return buildFrequencyMap(reviews, minLength).entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(keywordLimit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public List<KeywordSummary> extractTopKeywordsWithCount(List<String> reviews, int minLength, int keywordLimit) {
        return buildFrequencyMap(reviews, minLength).entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(keywordLimit)
                .map(entry -> new KeywordSummary(entry.getKey(), entry.getValue().longValue()))
                .collect(Collectors.toList());
    }

    public List<KeywordSummary> extractTopKeywordsForPerformance(Long performanceId) {
        String cacheKey = CACHE_PREFIX + performanceId;

        String cached = stringRedisTemplate.opsForValue().get(cacheKey);

        if(cached != null) {
            try {
                return objectMapper.readValue(cached, new TypeReference<List<KeywordSummary>>() {});
            } catch (Exception e) {
                log.warn("캐시 역직렬화 실패: {}", e.getMessage());
            }
        }

        List<String> reviews = reviewRepository.findByReservationPerformanceId(performanceId)
                .stream()
                .map(r -> (r.getSummary() != null && !r.getSummary().isBlank()) ? r.getSummary() : r.getContent())
                .toList();

        List<KeywordSummary> result = extractTopKeywordsWithCount(reviews,2,5);

        try {
            stringRedisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(result), Duration.ofHours(1));
        } catch (Exception e) {
            log.warn("캐시 직렬화 실패: {}", e.getMessage());
        }

        return result;
    }

    public void evictCache(Long performanceId) {
        stringRedisTemplate.delete(CACHE_PREFIX + performanceId);
    }
}
