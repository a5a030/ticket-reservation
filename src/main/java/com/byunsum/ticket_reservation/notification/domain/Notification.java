package com.byunsum.ticket_reservation.notification.domain;

import com.byunsum.ticket_reservation.member.domain.Member;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String message;
    private String link;

    @Column(name = "is_read")
    private boolean isRead;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Notification() {
    }

    public Notification(String message, Member member, NotificationType type) {
        this.message = message;
        this.member = member;
        this.type = type;
        this.link = null;
        this.isRead = false;
        this.createdAt = LocalDateTime.now();
    }

    public Notification(String message, Member member, String link, NotificationType type, boolean isRead, LocalDateTime createdAt) {
        this.message = message;
        this.member = member;
        this.link = link;
        this.type = type;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public String getMessage() {
        return message;
    }

    public String getLink() {
        return link;
    }

    public NotificationType getType() {
        return type;
    }

    public boolean isRead() {
        return isRead;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void markAsRead(boolean read) {
        this.isRead = read;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }
}
