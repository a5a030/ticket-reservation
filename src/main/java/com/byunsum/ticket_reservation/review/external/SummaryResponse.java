package com.byunsum.ticket_reservation.review.external;

public class SummaryResponse {
    private String summary;

    public SummaryResponse() {
    }

    public SummaryResponse(String summary) {
        this.summary = summary;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
