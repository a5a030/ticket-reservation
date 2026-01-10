package com.byunsum.ticket_reservation.ticket.domain;

import com.byunsum.ticket_reservation.reservation.domain.ReservationSeat;
import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_seat_id", nullable = false)
    private ReservationSeat reservationSeat;

    @Column(nullable = false, unique = true, length = 36)
    private String ticketCode;
    private String qrImageUrl;

    @Column(nullable = false)
    private LocalDateTime issuedAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    public Ticket() {
    }

    public Ticket(ReservationSeat reservationSeat, String ticketCode, String qrImageUrl, LocalDateTime issuedAt, LocalDateTime expiresAt, TicketStatus status) {
        this.reservationSeat = reservationSeat;
        this.ticketCode = ticketCode;
        this.qrImageUrl = qrImageUrl;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.status = status;
    }

    public static Ticket create(ReservationSeat reservationSeat, String  qrImageUrl, Duration validDuration) {
        String ticketCode = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        return  new Ticket(
                reservationSeat,
                ticketCode,
                qrImageUrl,
                now,
                now.plus(validDuration),
                TicketStatus.ISSUED
        );
    }

    public void invalidate() {
        if(!this.status.canTransitionTo(TicketStatus.INVALIDATED)) {
            throw new IllegalStateException("현재 상태에서 무효화 불가: "+this.status);
        }

        this.status = TicketStatus.INVALIDATED;
    }

    public Long getId() {
        return id;
    }

    public ReservationSeat getReservationSeat() {
        return reservationSeat;
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
        if (!this.status.canTransitionTo(TicketStatus.USED)) {
            throw new IllegalStateException("현재 상태에서 검표 불가: " + this.status);
        }

        this.status = TicketStatus.USED;
    }

    public void markExpired() {
        if(!this.status.canTransitionTo(TicketStatus.EXPIRED)) {
            throw new IllegalStateException("현재 상태에서는 만료 처리 불가: " + this.status);
        }

        this.status = TicketStatus.EXPIRED;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }

    public boolean isIssued() {
        return this.status ==  TicketStatus.ISSUED && !isExpired();
    }
}
