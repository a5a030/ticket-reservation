package com.byunsum.ticket_reservation.review.external;

import java.util.List;

public interface KeywordClient {
    List<String> extractKeywords(String text);
}
