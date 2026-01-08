package com.byunsum.ticket_reservation.review.dto;

public class KeywordSummary {
    private String keyword;
    private Long count;

    public KeywordSummary() {
    }

    public KeywordSummary(String keyword, Long count) {
        this.keyword = keyword;
        this.count = count;
    }

    public String getKeyword() {
        return keyword;
    }

    public Long getCount() {
        return count;
    }
}
