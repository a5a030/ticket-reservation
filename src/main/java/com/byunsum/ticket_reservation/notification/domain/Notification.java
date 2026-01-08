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

    @Column(nullable = false)
    private String message;
    private String link;

    @Column(nullable = false)
    private boolean read;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
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
        this.read = false;
    }

    public Notification(String message, Member member, String link, NotificationType type, boolean read) {
        this.message = message;
        this.member = member;
        this.link = link;
        this.type = type;
        this.read = read;
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
        return read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void markAsRead() {
        this.read = true;
    }

    public void markAsUnread() {
        this.read = false;
    }
}
