package com.byunsum.ticket_reservation.ticket.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "티켓 재발급 집계 응답 DTO")
public class TicketReissueStatsResponse {
    @Schema(description = "집계 기준 (회원 ID 또는 공연 제목)")
    private String label;

    @Schema(description = "재발급 횟수")
    private Long reissueCount;

    public TicketReissueStatsResponse(String label, Long reissueCount) {
        this.label = label;
        this.reissueCount = reissueCount;
    }

    public String getLabel() {
        return label;
    }

    public Long getReissueCount() {
        return reissueCount;
    }
}
