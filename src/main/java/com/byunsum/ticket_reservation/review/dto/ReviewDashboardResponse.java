package com.byunsum.ticket_reservation.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class ReviewDashboardResponse {
    @Schema(description = "공연 ID")
    private Long performanceId;

    @Schema(description = "공연 제목")
    private String performanceTitle;

    @Schema(description = "리뷰 통계 (긍/부정/중립, 평균 평점)")
    private ReviewStatisticsResponse statistics;

    @Schema(description = "키워드 TOP5")
    private List<KeywordSummary> keywords;

    @Schema(description = "대표 리뷰 예시")
    private List<ReviewResponse> sampleReviews;

    public ReviewDashboardResponse(Long performanceId, String performanceTitle,
                                   ReviewStatisticsResponse statistics,
                                   List<KeywordSummary> keywords,
                                   List<ReviewResponse> sampleReviews) {
        this.performanceId = performanceId;
        this.performanceTitle = performanceTitle;
        this.statistics = statistics;
        this.keywords = keywords;
        this.sampleReviews = sampleReviews;
    }

    public Long getPerformanceId() {
        return performanceId;
    }

    public String getPerformanceTitle() {
        return performanceTitle;
    }

    public ReviewStatisticsResponse getStatistics() {
        return statistics;
    }

    public List<KeywordSummary> getKeywords() {
        return keywords;
    }

    public List<ReviewResponse> getSampleReviews() {
        return sampleReviews;
    }
}
