package com.byunsum.ticket_reservation.question.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "문의 등록 요청 DTO")
public class QuestionRequestDto {
    @NotBlank
    @Schema(description = "문의 제목")
    private String title;

    @NotBlank
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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
