package com.byunsum.ticket_reservation.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.Map;

public record SalesStatsResponse(@Schema(description = "총 매출액")
                                 BigDecimal totalSales,

                                 @Schema(description = "총 결제 건수")
                                 long totalPayments,

                                 @Schema(description = "평균 결제 금액")
                                 BigDecimal averagePayment,

                                 @Schema(description = "공연별 매출 통계", example = "{\"Oasis Concert\": 500000000, \"IU Tour\": 300000000}")
                                 Map<String, BigDecimal> salesByPerformance,

                                 @Schema(description = "장르별 매출 통계", example = "{\"KPOP\": 700000000, \"ROCK\": 200000000}")
                                 Map<String, BigDecimal> salesByGenre) {

}