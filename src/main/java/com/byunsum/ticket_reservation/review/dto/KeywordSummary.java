package com.byunsum.ticket_reservation.review.dto;

public class KeywordSummary {
    private String keyword;
    private int count;

    public KeywordSummary() {
    }

    public KeywordSummary(String keyword, int count) {
        this.keyword = keyword;
        this.count = count;
    }

    public String getKeyword() {
        return keyword;
    }

    public int getCount() {
        return count;
    }
}
