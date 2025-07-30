package com.byunsum.ticket_reservation.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class ReservationRequest {
    @Schema(description = "예매할 공연 ID", example = "1")
    private Long performanceId;

    @Schema(description = "예매할 좌석 ID", example = "5")
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
