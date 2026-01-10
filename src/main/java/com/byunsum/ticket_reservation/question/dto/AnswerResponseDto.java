package com.byunsum.ticket_reservation.question.dto;

import com.byunsum.ticket_reservation.question.domain.Answer;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "답변 응답 DTO")
public class AnswerResponseDto {
    @Schema(description = "답변 ID", example = "1")
    private Long id;

    @Schema(description = "답변 내용")
    private String content;

    @Schema(description = "답변 작성 시간")
    private LocalDateTime createdAt;

    public AnswerResponseDto(Long id, String content, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
    }

    public AnswerResponseDto(Answer answer) {
        this.id = answer.getId();
        this.content = answer.getContent();
        this.createdAt = answer.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
