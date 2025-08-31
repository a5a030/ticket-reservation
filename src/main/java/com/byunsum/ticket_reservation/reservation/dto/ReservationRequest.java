package com.byunsum.ticket_reservation.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class ReservationRequest {
    @Schema(description = "예매할 회원 ID")
    private Long memberId;

    @Schema(description = "예매할 공연 ID", example = "1")
    private Long performanceId;

    @Schema(description = "예매할 좌석 ID 리스트", example = "[5, 6, 7]")
    private List<Long> seatIds;

    public ReservationRequest() {}

    public ReservationRequest(Long memberId, Long performanceId, List<Long> seatId) {
        this.memberId = memberId;
        this.performanceId = performanceId;
        this.seatIds = seatIds;
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

    public List<Long> getSeatIds() {
        return seatIds;
    }

    public void setSeatIds(List<Long> seatIds) {
        this.seatIds = seatIds;
    }
}
