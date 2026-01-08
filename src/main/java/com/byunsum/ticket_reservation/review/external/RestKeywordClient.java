package com.byunsum.ticket_reservation.review.external;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@Primary
public class RestKeywordClient implements KeywordClient {
    private static final Logger log = LoggerFactory.getLogger(RestKeywordClient.class);
    private final RestTemplate restTemplate;
    private static final String KEYWORD_API_URL = "http://localhost:5007/keywords";


    public RestKeywordClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<String> extractKeywords(String text) {
        Map<String, String> request = Map.of("text", text);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(KEYWORD_API_URL, request, Map.class);
            Map body = response.getBody();
            if(body == null) return Collections.emptyList();
            Object keywordsObj = body.get("keywords");
            if(!(keywordsObj instanceof List<?> list)) return Collections.emptyList();

            return list.stream().map(String::valueOf).toList();
        } catch (Exception e) {
            log.warn("키워드 서버 호출 실패: {}", e.getMessage());

            return Collections.emptyList();
        }
    }
}
