package com.byunsum.ticket_reservation.admin.dto;

import java.math.BigDecimal;

public record PaymentSummaryResponse(
        BigDecimal totalRevenue,
        long totalCount,
        BigDecimal averageAmount
) {}
