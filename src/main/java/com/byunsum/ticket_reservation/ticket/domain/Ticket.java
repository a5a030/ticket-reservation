package com.byunsum.ticket_reservation.ticket.domain;

import com.byunsum.ticket_reservation.reservation.domain.Reservation;
import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
public class Ticket {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    private String qrCode;

    private LocalDateTime issuedAt;
    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    public Ticket() {
    }

    public Ticket(Reservation reservation, String qrCode, LocalDateTime issuedAt, LocalDateTime expiresAt, TicketStatus status) {
        this.reservation = reservation;
        this.qrCode = qrCode;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.status = status;
    }

    public static Ticket create(Reservation reservation, String qrCode, Duration validDuration) {
        LocalDateTime now = LocalDateTime.now();

        return  new Ticket(
                reservation,
                qrCode,
                now,
                now.plus(validDuration),
                TicketStatus.ACTIVE
        );
    }

    public Long getId() {
        return id;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public String getQrCode() {
        return qrCode;
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

    public void setStatus(TicketStatus status) {
        this.status = status;
    }
}
