package com.byunsum.ticket_reservation.review.domain;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;

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

    public static SentimentType from(String value) {
        for(SentimentType type : values()) {
            if(type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }

        throw new CustomException(ErrorCode.INVALID_SENTIMENT);
    }
}
