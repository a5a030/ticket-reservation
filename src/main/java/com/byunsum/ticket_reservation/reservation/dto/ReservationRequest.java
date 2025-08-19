package com.byunsum.ticket_reservation.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class ReservationRequest {
    @Schema(description = "예매할 회원 ID")
    private Long memberId;

    @Schema(description = "예매할 공연 ID", example = "1")
    private Long performanceId;

    @Schema(description = "예매할 좌석 ID", example = "5")
    private Long seatId;

    public ReservationRequest() {}

    public ReservationRequest(Long memberId, Long performanceId, Long seatId) {
        this.memberId = memberId;
        this.performanceId = performanceId;
        this.seatId = seatId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

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
