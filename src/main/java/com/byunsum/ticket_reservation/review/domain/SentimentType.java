package com.byunsum.ticket_reservation.review.domain;

public enum SentimentType {
    POSITIVE("긍정"),
    NEGATIVE("부정"),
    NEUTRAL("중립");

    private final String displayName;

    SentimentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
