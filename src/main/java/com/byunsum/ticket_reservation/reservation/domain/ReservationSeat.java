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

    @Column(nullable = false)
    private int priceAtReservation;

    private Integer refundAmount;
    private Integer cancelFee;

    public ReservationSeat() {}

    public ReservationSeat(Reservation reservation, Seat seat) {
        this.reservation = reservation;
        this.seat = seat;
        this.priceAtReservation = seat.getPrice();
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
        cancel(0,0);
    }

    public void cancel(int cancelFee, int refundAmount) {
        if(this.status == ReservationStatus.CANCELLED) {
            return;
        }

        this.status = ReservationStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.cancelFee = cancelFee;
        this.refundAmount = refundAmount;
        seat.release();
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public int getPriceAtReservation() {
        return priceAtReservation;
    }

    public Integer getRefundAmount() {
        return refundAmount;
    }

    public Integer getCancelFee() {
        return cancelFee;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }
}
