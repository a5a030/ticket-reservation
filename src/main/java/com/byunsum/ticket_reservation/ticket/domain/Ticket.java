package com.byunsum.ticket_reservation.ticket.domain;

import com.byunsum.ticket_reservation.reservation.domain.Reservation;
import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Ticket {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    private String ticketCode;
    private String qrImageUrl;

    private LocalDateTime issuedAt;
    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    public Ticket() {
    }

    public Ticket(Reservation reservation, String ticketCode, String qrImageUrl, LocalDateTime issuedAt, LocalDateTime expiresAt, TicketStatus status) {
        this.reservation = reservation;
        this.ticketCode = ticketCode;
        this.qrImageUrl = qrImageUrl;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.status = status;
    }

    public static Ticket create(Reservation reservation, String  qrImageUrl, Duration validDuration) {
        String ticketCode = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        return  new Ticket(
                reservation,
                ticketCode,
                qrImageUrl,
                now,
                now.plus(validDuration),
                TicketStatus.ACTIVE
        );
    }

    public void updateQrCode(String ticketCode, String qrImageUrl, LocalDateTime issuedAt, LocalDateTime expiresAt) {
        this.ticketCode = ticketCode;
        this.qrImageUrl = qrImageUrl;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
    }

    public Long getId() {
        return id;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public String getTicketCode() {
        return ticketCode;
    }

    public String getQrImageUrl() {
        return qrImageUrl;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void markUsed() {
        this.status = TicketStatus.USED;
    }

    public void markExpired() {
        this.status = TicketStatus.EXPIRED;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }

    public boolean isActive() {
        return this.status ==  TicketStatus.ACTIVE && !isExpired();
    }
}
