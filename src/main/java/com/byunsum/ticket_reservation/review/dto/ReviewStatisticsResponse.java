package com.byunsum.ticket_reservation.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "공연 리뷰 통계 응답")
public class ReviewStatisticsResponse {
    @Schema(description = "공연 ID")
    private Long performanceId;

    @Schema(description = "긍정 리뷰 수")
    private long positiveCount;

    @Schema(description = "부정 리뷰 수")
    private long negativeCount;

    @Schema(description = "중립 리뷰 수")
    private long neutralCount;

    @Schema(description = "평균 평점")
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
