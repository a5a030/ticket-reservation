package com.byunsum.ticket_reservation.reservation.dto;

public class ReservationRequest {
    private Long performanceId;
    private Long seatId;

    public ReservationRequest() {}

    public Long getPerformanceId() {
        return performanceId;
    }

    public void setPerformanceId(Long performanceId) {
        this.performanceId = performanceId;
    }

    public Long getSeatId() {
        return seatId;
    }

    public void setSeatId(Long seatId) {
        this.seatId = seatId;
    }
}
