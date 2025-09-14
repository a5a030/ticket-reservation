package com.byunsum.ticket_reservation.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "공연/장르별 결제 통계 응답")
public record PaymentSalesStatsResponse(
        @Schema(description = "그룹 라벨 (공연명 or 장르명)")
        String groupLabel,

        @Schema(description = "총 매출액")
        BigDecimal totalAmount,

        @Schema(description = "결제 건수")
        Long count
) {}
