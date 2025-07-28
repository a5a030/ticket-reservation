package com.byunsum.ticket_reservation.question.dto;

import java.time.LocalDateTime;

public class AnswerResponseDto {
    private Long id;
    private String content;
    private LocalDateTime createdAt;

    public AnswerResponseDto(Long id, String content, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
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
