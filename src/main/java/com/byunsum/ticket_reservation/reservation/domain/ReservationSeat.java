package com.byunsum.ticket_reservation.reservation.domain;

import com.byunsum.ticket_reservation.seat.domain.Seat;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class ReservationSeat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    private Seat seat;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status = ReservationStatus.CONFIRMED;

    private LocalDateTime cancelledAt;

    public ReservationSeat() {}

    public ReservationSeat(Reservation reservation, Seat seat) {
        this.reservation = reservation;
        this.seat = seat;
        reservation.addReservationSeat(this);
    }

    public Long getId() {
        return id;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public Seat getSeat() {
        return seat;
    }

    public void cancel() {
        if(this.status == ReservationStatus.CANCELLED) {
            return;
        }

        this.status = ReservationStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        seat.release();
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }
}
