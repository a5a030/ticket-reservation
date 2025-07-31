package com.byunsum.ticket_reservation.question.dto;

import com.byunsum.ticket_reservation.question.domain.Question;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Schema(description = "문의 응답 DTO")
public class QuestionResponseDto {

    @Schema(description = "문의 ID", example = "1")
    private Long id;

    @Schema(description = "작성 일시")
    private LocalDateTime createdAt;

    public QuestionResponseDto(Question question) {
        this.id = question.getId();
        this.createdAt = question.getCreatedAt();
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
