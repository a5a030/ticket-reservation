package com.byunsum.ticket_reservation.reservation.dto;

import java.util.List;

public class BatchReservationRequest {
    private Long memberId;
    private Long performanceId;
    private List<Long> seatIds;

    public BatchReservationRequest() {
    }

    public BatchReservationRequest(Long memberId, Long performanceId, List<Long> seatIds) {
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
