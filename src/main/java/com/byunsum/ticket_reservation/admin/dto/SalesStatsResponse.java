package com.byunsum.ticket_reservation.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record SalesStatsResponse(@Schema(description = "총 매출액")
                                 BigDecimal totalSales,

                                 @Schema(description = "총 결제 건수")
                                 long totalPayments,

                                 @Schema(description = "평균 결제 금액")
                                 BigDecimal averagePayment) {
}
