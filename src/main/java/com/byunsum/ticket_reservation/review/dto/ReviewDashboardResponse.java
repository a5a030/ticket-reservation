package com.byunsum.ticket_reservation.review.dto;

import java.util.List;
import java.util.Map;

public class ReviewDashboardResponse {
    private Long performanceId;
    private String performanceTitle;
    private int totalCount;
    private int positiveCount;
    private int negativeCount;
    private int neutralCount;
    private double positiveRatio;
    private double averageRating;

    private List<KeywordSummary> topKeywords;

    private Map<String, List<String>> examples;

    public ReviewDashboardResponse() {
    }

    public ReviewDashboardResponse(Long performanceId, String performanceTitle, int totalCount, int positiveCount, int negativeCount, int neutralCount, double positiveRatio, double averageRating, List<KeywordSummary> topKeywords, Map<String, List<String>> examples) {
        this.performanceId = performanceId;
        this.performanceTitle = performanceTitle;
        this.totalCount = totalCount;
        this.positiveCount = positiveCount;
        this.negativeCount = negativeCount;
        this.neutralCount = neutralCount;
        this.positiveRatio = positiveRatio;
        this.averageRating = averageRating;
        this.topKeywords = topKeywords;
        this.examples = examples;
    }

    public Long getPerformanceId() {
        return performanceId;
    }

    public String getPerformanceTitle() {
        return performanceTitle;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getPositiveCount() {
        return positiveCount;
    }

    public int getNegativeCount() {
        return negativeCount;
    }

    public int getNeutralCount() {
        return neutralCount;
    }

    public double getPositiveRatio() {
        return positiveRatio;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public List<KeywordSummary> getTopKeywords() {
        return topKeywords;
    }

    public Map<String, List<String>> getExamples() {
        return examples;
    }
}
