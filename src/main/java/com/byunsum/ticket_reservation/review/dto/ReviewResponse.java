package com.byunsum.ticket_reservation.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "리뷰 조회 응답 DTO")
public class ReviewResponse {
    @Schema(description = "리뷰 ID", example = "1")
    private Long id;

    @Schema(description = "연결된 예매 ID", example = "5")
    private Long reservationId;

    @Schema(description = "리뷰 내용", example = "정말 좋았어요")
    private String content;

    @Schema(description = "별점", example = "4")
    private int rating;

    @Schema(description = "작성일시")
    private LocalDateTime createdAt;

    public ReviewResponse() {
    }

    public ReviewResponse(Long id, Long reservationId, String content, int rating, LocalDateTime createdAt) {
        this.id = id;
        this.reservationId = reservationId;
        this.content = content;
        this.rating = rating;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public String getContent() {
        return content;
    }

    public int getRating() {
        return rating;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
