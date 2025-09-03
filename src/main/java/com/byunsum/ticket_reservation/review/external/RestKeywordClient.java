package com.byunsum.ticket_reservation.review.external;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class RestKeywordClient implements KeywordClient {
    private final RestTemplate restTemplate = new RestTemplate();
    private final String URL = "http://localhost:5007/keywords";

    @Override
    public List<String> extractKeywords(String text) {
        Map<String, String> request = Map.of("text", text);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(URL, request, Map.class);

            return (List<String>) response.getBody().getOrDefault("keywords", Collections.emptyList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
