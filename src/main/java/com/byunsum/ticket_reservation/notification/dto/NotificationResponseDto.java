package com.byunsum.ticket_reservation.notification.dto;

import com.byunsum.ticket_reservation.notification.domain.Notification;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class NotificationResponseDto {
    @Schema(description = "알림 ID", example = "42")
    private Long id;

    @Schema(description = "알림 메시지")
    private String message;

    @Schema(description = "알림 클릭 시 이동할 링크")
    private String link;

    @Schema(description = "알림 읽음 여부", example = "false")
    private  boolean isRead;

    @Schema(description = "알림 생성 일시")
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
