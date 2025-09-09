package com.byunsum.ticket_reservation.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "공연/장르별 결제 통계 응답")
public class PaymentSalesStatsResponse {
    @Schema(description = "그룹 라벨 (공연명 or 장르명)")
    private String label;

    @Schema(description = "총 매출액")
    private Long totalAmount;

    @Schema(description = "결제 건수")
    private Long count;

    public PaymentSalesStatsResponse(String label, Long totalAmount, Long count) {
        this.label = label;
        this.totalAmount = totalAmount;
        this.count = count;
    }

    public String getLabel() {
        return label;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public Long getCount() {
        return count;
    }
}
