package com.byunsum.ticket_reservation.question.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private String content;

    private LocalDateTime createdAt;

    @OneToOne
    @JoinColumn(name = "question_id")
    private Question question;

    public Answer() {
    }

    public Answer(String content, Question question) {
        this.content = content;
        this.question = question;
    }

    @PostPersist
    public void postPersist() {
        this.createdAt = LocalDateTime.now();
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

    public Question getQuestion() {
        return question;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }
}
