package com.byunsum.ticket_reservation.admin.dto;

import com.byunsum.ticket_reservation.ticket.dto.VerificationStatsResponse;

public record DashboardResponse(SalesStatsResponse sales,
                                ReviewStatsResponse reviews,
                                VerificationStatsResponse tickets) {
}
