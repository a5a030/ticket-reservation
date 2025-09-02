package com.byunsum.ticket_reservation.seat.domain;

import com.byunsum.ticket_reservation.performance.domain.Performance;
import com.byunsum.ticket_reservation.performance.domain.PerformanceRound;
import jakarta.persistence.*;

@Entity
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String seatNo;
    private int price;
    private boolean isReserved;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_round_id")
    private PerformanceRound performanceRound;

    public Seat() {
    }

    public Seat(String seatNo, int price, boolean isReserved, PerformanceRound performanceRound) {
        this.seatNo = seatNo;
        this.price = price;
        this.isReserved = isReserved;
        this.performanceRound = performanceRound;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSeatNo() {
        return seatNo;
    }

    public void setSeatNo(String seatNo) {
        this.seatNo = seatNo;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isReserved() {
        return isReserved;
    }

    public void setReserved(boolean reserved) {
        isReserved = reserved;
    }

    public PerformanceRound getPerformanceRound() {
        return performanceRound;
    }

    public void setPerformanceRound(PerformanceRound performanceRound) {
        this.performanceRound = performanceRound;
    }

    public void release() {
        this.isReserved = false;
    }
}
