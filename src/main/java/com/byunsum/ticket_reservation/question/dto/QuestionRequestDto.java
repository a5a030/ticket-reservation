package com.byunsum.ticket_reservation.question.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "문의 등록 요청 DTO")
public class QuestionRequestDto {
    @Schema(description = "문의 제목")
    private String title;

    @Schema(description = "문의 내용")
    private String content;

    public QuestionRequestDto() {
    }

    public QuestionRequestDto(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }


    public String getContent() {
        return content;
    }
}
