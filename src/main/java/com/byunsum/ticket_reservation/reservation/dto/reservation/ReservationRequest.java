package com.byunsum.ticket_reservation.reservation.dto.reservation;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class ReservationRequest {
    @Schema(description = "예매할 공연 ID", example = "1")
    private Long performanceId;

    @Schema(description = "예매할 좌석 ID 리스트", example = "[5, 6, 7]")
    private List<Long> seatIds;

    public ReservationRequest() {}

    public ReservationRequest(Long performanceId, List<Long> seatIds) {
        this.performanceId = performanceId;
        this.seatIds = seatIds;
    }

    public Long getPerformanceId() {
        return performanceId;
    }

    public void setPerformanceId(Long performanceId) {
        this.performanceId = performanceId;
    }

    public List<Long> getSeatIds() {
        return seatIds;
    }

    public void setSeatIds(List<Long> seatIds) {
        this.seatIds = seatIds;
    }
}
