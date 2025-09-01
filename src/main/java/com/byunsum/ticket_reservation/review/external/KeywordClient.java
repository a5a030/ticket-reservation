package com.byunsum.ticket_reservation.review.external;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class KeywordClient {
    private final RestTemplate restTemplate = new RestTemplate();

    public List<String> extractKeywords(String keyword) {
        String url = "";
        Map<String, String> request = Map.of("keyword", keyword);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            return (List<String>) response.getBody().get("keywords");
        } catch (Exception e) {
            return List.of();
        }
    }
}
