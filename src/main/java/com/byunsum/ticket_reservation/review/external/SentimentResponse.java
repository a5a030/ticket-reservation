package com.byunsum.ticket_reservation.review.external;

public class SentimentResponse {
    private String sentiment;
    private double score;

    public SentimentResponse() {
    }

    public SentimentResponse(String sentiment, double score) {
        this.sentiment = sentiment;
        this.score = score;
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
