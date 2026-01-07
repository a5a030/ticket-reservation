package com.byunsum.ticket_reservation.seat.domain;

import com.byunsum.ticket_reservation.performance.domain.PerformanceRound;
import jakarta.persistence.*;

@Table(
        uniqueConstraints = @UniqueConstraint(
                name = "uk_seat_round_seat_no",
                columnNames = {"performance_round_id", "seat_no"}
        ),
        indexes = {
                @Index(name = "idx_seat_round", columnList = "performance_round_id"),
                @Index(name = "idx_seat_round_reserved", columnList = "performance_round_id,reserved")
        }
)
@Entity
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seat_no", nullable = false, length = 20)
    private String seatNo;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private boolean reserved;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_round_id", nullable = false)
    private PerformanceRound performanceRound;

    public Seat() {
    }


    public Seat(String seatNo, int price, PerformanceRound round) {
        if(round == null) {
            throw new IllegalArgumentException("round required");
        }

        if(price<0) {
            throw new IllegalArgumentException("price must be >=0");
        }

        this.seatNo = normalizedSeatNo(seatNo);
        this.price = price;
        this.reserved = false;
        this.performanceRound = round;
    }

    public Long getId() {
        return id;
    }

    public String getSeatNo() {
        return seatNo;
    }

    public void setSeatNo(String seatNo) {
        this.seatNo = normalizedSeatNo(seatNo);
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isReserved() {
        return reserved;
    }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
    }

    public PerformanceRound getPerformanceRound() {
        return performanceRound;
    }

    public void release() {
        this.reserved = false;
    }

    private String normalizedSeatNo(String seatNo) {
        if(seatNo == null) {
            throw new IllegalArgumentException("seatNo required");
        }

        String normalizedSeatNo = seatNo.trim().toUpperCase();

        if(normalizedSeatNo.isBlank()) {
            throw new IllegalArgumentException("seatNo required");
        }

        return normalizedSeatNo;
    }
}
