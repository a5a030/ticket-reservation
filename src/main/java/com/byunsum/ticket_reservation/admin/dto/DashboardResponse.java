package com.byunsum.ticket_reservation.admin.dto;

import com.byunsum.ticket_reservation.ticket.dto.VerificationStatsResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "관리자 대시보드 통합 응답")
public record DashboardResponse(
        @Schema(description = "매출 통계")
        SalesStatsResponse sales,

        @Schema(description = "리뷰 통계")
        ReviewStatsResponse reviews,

        @Schema(description = "티켓 검증 통계")
        VerificationStatsResponse tickets) {
}
