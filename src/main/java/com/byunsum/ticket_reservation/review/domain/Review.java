package com.byunsum.ticket_reservation.review.domain;

import com.byunsum.ticket_reservation.reservation.domain.Reservation;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", unique = true)
    private Reservation reservation;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private int rating;

    private String summary; // ai 요약
    private String sentiment; // 감정 분석 결과

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Review() {
    }

    public Review(Reservation reservation, String content, int rating) {
        this.reservation = reservation;
        this.content = content;
        this.rating = rating;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Review(Reservation reservation, String content, int rating, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.reservation = reservation;
        this.content = content;
        this.rating = rating;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void update(String content, int rating) {
        this.content = content;
        this.rating = rating;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateAI(String summary, String sentiment) {
        this.summary = summary;
        this.sentiment = sentiment;
    }

    public Long getId() {
        return id;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public String getContent() {
        return content;
    }

    public int getRating() {
        return rating;
    }

    public String getSummary() {
        return summary;
    }

    public String getSentiment() {
        return sentiment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}