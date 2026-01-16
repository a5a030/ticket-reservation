package com.byunsum.ticket_reservation.reservation.domain;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
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
    private ReservationSeatStatus status = ReservationSeatStatus.HOLD;

    private LocalDateTime holdExpiredAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime releasedAt;

    @Column(nullable = false)
    private int priceAtReservation;

    private Integer refundAmount;
    private Integer cancelFee;

    public ReservationSeat() {}

    public ReservationSeat(Reservation reservation, Seat seat, LocalDateTime holdExpiredAt) {
        this.reservation = reservation;
        this.seat = seat;
        this.priceAtReservation = seat.getPrice();
        this.holdExpiredAt = holdExpiredAt;
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

    public void confirm(LocalDateTime now) {
        status.assertTransitionTo(ReservationSeatStatus.CONFIRMED);

        if(status != ReservationSeatStatus.HOLD) {
            throw new CustomException(ErrorCode.INVALID_SEAT_STATUS);
        }

        if(holdExpiredAt != null && !now.isBefore(holdExpiredAt)) {
            throw new CustomException(ErrorCode.SEAT_HOLD_EXPIRED);
        }

        this.status = ReservationSeatStatus.CONFIRMED;
        this.confirmedAt = now;
    }

    public void cancel(int cancelFee, int refundAmount, LocalDateTime now) {
        status.assertTransitionTo(ReservationSeatStatus.CANCELLED);

        if(this.status == ReservationSeatStatus.CANCELLED || this.status == ReservationSeatStatus.RELEASED) {
            return;
        }

        this.status = ReservationSeatStatus.CANCELLED;
        this.cancelledAt = now;
        this.cancelFee = cancelFee;
        this.refundAmount = refundAmount;
        seat.release();
    }

    public void release(LocalDateTime now) {
        status.assertTransitionTo(ReservationSeatStatus.RELEASED);

        if(status == ReservationSeatStatus.RELEASED ||status == ReservationSeatStatus.CANCELLED) {
            return;
        }

        // HOLD -> RELEASED는 만료 근거가 있어야 함
        if(status == ReservationSeatStatus.HOLD) {
            if(holdExpiredAt == null || now.isBefore(holdExpiredAt)) {
                throw new CustomException(ErrorCode.INVALID_RELEASE_REQUEST);
            }
        }

        this.status = ReservationSeatStatus.RELEASED;
        this.releasedAt = now;

        this.cancelFee = 0;
        this.refundAmount = 0;

        seat.release();
    }

    public ReservationSeatStatus getStatus() {
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
