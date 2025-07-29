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
    private boolean isRead = false;
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Notification() {
    }

    public Notification(String message, Member member) {
        this.message = message;
        this.member = member;
        this.link = null;
        this.isRead = false;
        this.createdAt = LocalDateTime.now();
    }

    public Notification(String message, Member member, String link, boolean isRead, LocalDateTime createdAt) {
        this.message = message;
        this.member = member;
        this.link = link;
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

    public boolean isRead() {
        return isRead;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void markAsRead() {
        this.isRead = true;
    }
}
