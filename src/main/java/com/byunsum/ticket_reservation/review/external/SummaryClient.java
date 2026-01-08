package com.byunsum.ticket_reservation.review.external;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class SummaryClient {
    private static final Logger log = LoggerFactory.getLogger(SummaryClient.class);

    private final RestTemplate restTemplate;
    private static final String SUMMARY_API_URL = "http://localhost:5006/summarize";

    public SummaryClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public SummaryResponse getSummary(String text) {
        Map<String, String> body = new HashMap<>();
        body.put("text", text);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<SummaryResponse> response = restTemplate.postForEntity(SUMMARY_API_URL, request, SummaryResponse.class);

            return response.getBody();
        } catch (Exception e) {
            log.warn("요약 서버 호출 실패: {}", e.getMessage());
            return new SummaryResponse("요약할 수 없습니다.");
        }
    }

    public String getSummaryUrl() {
        return SUMMARY_API_URL;
    }
}
