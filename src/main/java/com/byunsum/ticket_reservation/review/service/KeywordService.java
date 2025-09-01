package com.byunsum.ticket_reservation.review.service;

import com.byunsum.ticket_reservation.review.domain.Review;
import com.byunsum.ticket_reservation.review.dto.KeywordSummary;
import com.byunsum.ticket_reservation.review.repository.ReviewRepository;
import org.openkoreantext.processor.KoreanTokenJava;
import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import org.openkoreantext.processor.tokenizer.KoreanTokenizer;
import org.springframework.stereotype.Service;
import scala.collection.Seq;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class KeywordService {
    private final ReviewRepository reviewRepository;

    public KeywordService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
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
                .map(entry -> new KeywordSummary(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public List<KeywordSummary> extractTopKeywordsForPerformance(Long performanceId) {
        List<String> reviews = reviewRepository.findByReservationPerformanceId(performanceId)
                .stream().map(Review::getContent).toList();

        return extractTopKeywordsWithCount(reviews, 2, 5);
    }
}
