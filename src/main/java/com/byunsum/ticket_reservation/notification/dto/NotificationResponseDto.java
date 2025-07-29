package com.byunsum.ticket_reservation.notification.dto;

import com.byunsum.ticket_reservation.notification.domain.Notification;

import java.time.LocalDateTime;

public class NotificationResponseDto {
    private Long id;
    private String message;
    private String link;
    private  boolean isRead;
    private LocalDateTime createdAt;

    public NotificationResponseDto(Notification notification) {
        this.id = notification.getId();
        this.message = notification.getMessage();
        this.link = notification.getLink();
        this.isRead = notification.isRead();
        this.createdAt = notification.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getLink() {
        return link;
    }

    public boolean isRead() {
        return isRead;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
