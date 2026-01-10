package com.byunsum.ticket_reservation.question.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "답변 요청 DTO")
public class AnswerRequestDto {
    @Schema(description = "답변 내용", example = "문의 주신 내용에 대한 답변입니다.")
    @NotBlank(message = "답변 내용은 비어 있을 수 없습니다.")
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
