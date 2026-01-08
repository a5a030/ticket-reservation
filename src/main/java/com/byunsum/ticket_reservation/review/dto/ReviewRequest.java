package com.byunsum.ticket_reservation.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "리뷰 작성 요청 DTO")
public class ReviewRequest {
    @NotNull
    @Schema(description = "연결된 예매 ID", example = "1")
    private Long reservationId;

    @NotBlank @Size(max = 1000)
    @Schema(description = "리뷰 내용", example = "정말 감동적인 공연이었어요!")
    private String content;

    @Min(1) @Max(5)
    @Schema(description = "별점 (1~5)", example = "5")
    private int rating;

    public ReviewRequest() {
    }

    public ReviewRequest(Long reservationId, String content, int rating) {
        this.reservationId = reservationId;
        this.content = content;
        this.rating = rating;
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
}
