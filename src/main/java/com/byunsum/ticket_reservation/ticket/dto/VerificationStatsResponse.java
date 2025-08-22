package com.byunsum.ticket_reservation.ticket.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

public record VerificationStatsResponse(
        @Schema(description = "검증성공률", example = "0.92")
        double successRate,

        @Schema(description = "검증 결과 카운트", example = "{\"SUCCESS\": 120, \"FAIL\": 15}")
        Map<String, Long> resultCounts,

        @Schema(description = "시간대별 검증 건수", example = "{\"10\": 12, \"11\": 20, \"12\": 5}")
        Map<Integer, Long> hourlyCounts
) {
}
