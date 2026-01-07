package com.byunsum.ticket_reservation.admin.dto;

import com.byunsum.ticket_reservation.payment.dto.PaymentSalesStatsResponse;

import java.util.List;

public record PaymentDashboardCardsResponse(
        List<PaymentSalesStatsResponse> topPerformances,
        List<PaymentSalesStatsResponse> topGenres
) { }
