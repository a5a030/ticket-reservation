package com.byunsum.ticket_reservation.reservation.domain;

import com.byunsum.ticket_reservation.performance.domain.Performance;
import com.byunsum.ticket_reservation.seat.domain.Seat;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reservationCode;

    @ManyToOne
    @JoinColumn(name = "performance_id")
    private Performance performance;

    @OneToOne
    @JoinColumn(name = "seat_id")
    private Seat seat;

    private boolean isCanceled = false;

    //예매일시
    private LocalDateTime createdAt;

    public Reservation() {
    }

    public Reservation(Performance performance, Seat seat) {
        this.performance = performance;
        this.seat = seat;
        this.reservationCode = UUID.randomUUID().toString(); //예매번호 자동 생성
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getReservationCode() {
        return reservationCode;
    }

    public Performance getPerformance() {
        return performance;
    }

    public Seat getSeat() {
        return seat;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public void cancel() {
        this.isCanceled = true;
    }
}
