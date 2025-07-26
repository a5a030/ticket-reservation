package com.byunsum.ticket_reservation.question.dto;

public class QuestionRequestDto {
    private String title;
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
