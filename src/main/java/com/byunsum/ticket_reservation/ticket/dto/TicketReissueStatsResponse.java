package com.byunsum.ticket_reservation.ticket.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "티켓 재발급 집계 응답 DTO")
public class TicketReissueStatsResponse {
    @Schema(description = "회원 로그인 ID")
    private String loginId;

    @Schema(description = "재발급 횟수")
    private Long reissueCount;

    public TicketReissueStatsResponse(String loginId, Long reissueCount) {
        this.loginId = loginId;
        this.reissueCount = reissueCount;
    }

    public String getLoginId() {
        return loginId;
    }

    public Long getReissueCount() {
        return reissueCount;
    }
}
