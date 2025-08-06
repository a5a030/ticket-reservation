package com.byunsum.ticket_reservation.review.dto;

public class ReviewStatisticsResponse {
    private Long performanceId;
    private long positiveCount;
    private long negativeCount;
    private long neutralCount;
    private double averageRating;

    public ReviewStatisticsResponse(Long performanceId, long positiveCount, long negativeCount, long neutralCount, double averageRating) {
        this.performanceId = performanceId;
        this.positiveCount = positiveCount;
        this.negativeCount = negativeCount;
        this.neutralCount = neutralCount;
        this.averageRating = averageRating;
    }

    public Long getPerformanceId() {
        return performanceId;
    }

    public long getPositiveCount() {
        return positiveCount;
    }

    public long getNegativeCount() {
        return negativeCount;
    }

    public long getNeutralCount() {
        return neutralCount;
    }

    public double getAverageRating() {
        return averageRating;
    }
}
