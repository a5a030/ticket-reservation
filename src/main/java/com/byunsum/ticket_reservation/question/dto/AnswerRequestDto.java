package com.byunsum.ticket_reservation.question.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "답변 요청 DTO")
public class AnswerRequestDto {
    private String content;

    @Schema(description = "답변 내용")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
