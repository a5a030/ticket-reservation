package com.byunsum.ticket_reservation.ticket.domain;

import com.byunsum.ticket_reservation.reservation.domain.reservation.ReservationSeat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class TicketReissueLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_seat_id", nullable = false)
    private ReservationSeat reservationSeat;

    @Column(nullable = false, length = 36)
    private String oldTicketCode;
    @Column(nullable = false, length = 36)
    private String newTicketCode;

    @Column(nullable = false)
    private String actorLoginId;
    @Column(nullable = false)
    private String actorName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReissueReason reissueReason;

    @Column(nullable = false)
    private LocalDateTime reissueAt;

    public TicketReissueLog() {
    }

    public TicketReissueLog(ReservationSeat reservationSeat, String oldTicketCode, String newTicketCode, String actorLoginId, String actorName, ReissueReason reason, LocalDateTime reissueAt) {
        this.reservationSeat = reservationSeat;
        this.oldTicketCode = oldTicketCode;
        this.newTicketCode = newTicketCode;
        this.actorLoginId = actorLoginId;
        this.actorName = actorName;
        this.reissueReason = reason;
        this.reissueAt = reissueAt;
    }

    public Long getId() {
        return id;
    }

    @JsonIgnore
    public ReservationSeat getReservationSeat() {
        return reservationSeat;
    }

    public String getOldTicketCode() {
        return oldTicketCode;
    }

    public String getNewTicketCode() {
        return newTicketCode;
    }

    public String getActorLoginId() {
        return actorLoginId;
    }

    public String getActorName() {
        return actorName;
    }

    public ReissueReason getReissueReason() {
        return reissueReason;
    }

    public LocalDateTime getReissueAt() {
        return reissueAt;
    }
}
