package com.byunsum.ticket_reservation.payment.dto;

import com.byunsum.ticket_reservation.payment.domain.PaymentCancelReason;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class PaymentCancelRequest {
    @NotNull
    @Schema(description = "취소사유")
    private PaymentCancelReason reason;

    @Schema(description = "부분 취소 금액(전체 취소 시 null 또는 결제 금액)")
    private BigDecimal cancelAmount;

    public PaymentCancelRequest() {
    }

    public PaymentCancelRequest(PaymentCancelReason reason, BigDecimal cancelAmount) {
        this.reason = reason;
        this.cancelAmount = cancelAmount;
    }

    public PaymentCancelReason getReason() {
        return reason;
    }

    public BigDecimal getCancelAmount() {
        return cancelAmount;
    }
}
