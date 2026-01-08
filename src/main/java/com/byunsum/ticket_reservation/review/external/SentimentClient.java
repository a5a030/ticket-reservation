package com.byunsum.ticket_reservation.review.external;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class SentimentClient {
    private static final Logger log = LoggerFactory.getLogger(SentimentClient.class);

    private final RestTemplate restTemplate;
    private static final String SENTIMENT_API_URL = "http://localhost:5005/analyze";

    public SentimentClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public SentimentResponse analyzeSentiment(String text) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> body = new HashMap<>();
            body.put("text", text);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            ResponseEntity<SentimentResponse> response = restTemplate.exchange(
                    SENTIMENT_API_URL, HttpMethod.POST, request, SentimentResponse.class
            );

            return response.getBody();
        } catch (Exception e) {
            log.warn("감정 분석 서버 호출 실패: {}", e.getMessage());

            return new SentimentResponse("NEUTRAL", 0.0); //fallback
        }
    }
}
