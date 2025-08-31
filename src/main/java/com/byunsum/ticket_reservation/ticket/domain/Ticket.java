package com.byunsum.ticket_reservation.ticket.domain;

import com.byunsum.ticket_reservation.reservation.domain.Reservation;
import com.byunsum.ticket_reservation.reservation.domain.ReservationSeat;
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
    @JoinColumn(name = "reservation_seat_id", nullable = false)
    private ReservationSeat reservationSeat;

    private String ticketCode;
    private String qrImageUrl;

    private LocalDateTime issuedAt;
    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

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
                TicketStatus.ACTIVE
        );
    }

    public void refresh(String newTicketCode, String newQrImageUrl, Duration validDuration) {
        //기존 티켓 무효화
        this.status = TicketStatus.CANCELLED;

        //새로 갱신
        this.ticketCode = newTicketCode;
        this.qrImageUrl = newQrImageUrl;
        this.issuedAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plus(validDuration);
        this.status = TicketStatus.ACTIVE;
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
