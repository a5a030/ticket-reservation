package com.byunsum.ticket_reservation.seat.domain;

import com.byunsum.ticket_reservation.performance.domain.Performance;
import jakarta.persistence.*;

@Entity
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String seatNo;
    private int price;
    private boolean isReserved;

    @ManyToOne
    @JoinColumn(name = "performance_id")
    private Performance performance;

    public Seat() {
    }

    public Seat(String seatNo, int price, boolean isReserved, Performance performance) {
        this.seatNo = seatNo;
        this.price = price;
        this.isReserved = isReserved;
        this.performance = performance;
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

    public Performance getPerformance() {
        return performance;
    }

    public void setPerformance(Performance performance) {
        this.performance = performance;
    }

    public void release() {
        this.isReserved = false;
    }
}
