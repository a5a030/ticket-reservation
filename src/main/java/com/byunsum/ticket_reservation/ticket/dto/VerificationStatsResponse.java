package com.byunsum.ticket_reservation.ticket.dto;

import java.util.Map;

public record VerificationStatsResponse(
        double successRate,
        Map<String, Long> resultCounts,
        Map<Integer, Long> hourlyCounts
) {
}
