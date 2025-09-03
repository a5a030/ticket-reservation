package com.byunsum.ticket_reservation.review.external;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@Primary
public class FallbackKeywordClient implements KeywordClient {
    @Override
    public List<String> extractKeywords(String text) {
        return Collections.emptyList();
    }
}
