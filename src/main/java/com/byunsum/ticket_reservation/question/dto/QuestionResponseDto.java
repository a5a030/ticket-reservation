package com.byunsum.ticket_reservation.question.dto;

import com.byunsum.ticket_reservation.question.domain.Question;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class QuestionResponseDto {
    private Long id;
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
