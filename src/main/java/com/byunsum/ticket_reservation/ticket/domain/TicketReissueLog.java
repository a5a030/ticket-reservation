package com.byunsum.ticket_reservation.ticket.domain;

import com.byunsum.ticket_reservation.reservation.domain.ReservationSeat;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class TicketReissueLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_seat_id")
    private ReservationSeat reservationSeat;

    private String oldTicketCode;
    private String newTicketCode;
    private String loginId;
    private String username;
    private LocalDateTime reissueAt;

    public TicketReissueLog() {
    }

    public TicketReissueLog(ReservationSeat reservationSeat, String oldTicketCode, String newTicketCode, String loginId, String username, LocalDateTime reissueAt) {
        this.reservationSeat = reservationSeat;
        this.oldTicketCode = oldTicketCode;
        this.newTicketCode = newTicketCode;
        this.loginId = loginId;
        this.username = username;
        this.reissueAt = reissueAt;
    }

    public Long getId() {
        return id;
    }

    public ReservationSeat getReservationSeat() {
        return reservationSeat;
    }

    public String getOldTicketCode() {
        return oldTicketCode;
    }

    public String getNewTicketCode() {
        return newTicketCode;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getUsername() {
        return username;
    }

    public LocalDateTime getReissueAt() {
        return reissueAt;
    }
}
