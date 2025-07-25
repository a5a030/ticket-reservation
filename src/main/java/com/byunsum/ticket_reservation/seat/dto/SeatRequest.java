package com.byunsum.ticket_reservation.seat.dto;

import com.byunsum.ticket_reservation.performance.domain.Performance;

public class SeatRequest {
    private String seatNo;
    private int price;
    private Long performanceId;

    public SeatRequest() {
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

    public Long getPerformanceId() {
        return performanceId;
    }

    public void setPerformanceId(Long performanceId) {
        this.performanceId = performanceId;
    }
}
