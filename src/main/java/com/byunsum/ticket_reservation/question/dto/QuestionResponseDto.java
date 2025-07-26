package com.byunsum.ticket_reservation.question.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class QuestionResponseDto {
    private Long id;
    private String createdAt;

    public QuestionResponseDto(Long id, LocalDateTime createdAt) {
        this.id = id;
        this.createdAt = formatDateTime(createdAt);
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

    public String getCreatedAt() {
        return createdAt;
    }
}
