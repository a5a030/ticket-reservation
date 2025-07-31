package com.byunsum.ticket_reservation.payment.dto;

import com.byunsum.ticket_reservation.payment.domain.PaymentMethod;
import com.byunsum.ticket_reservation.payment.domain.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class PaymentResponse {
    @Schema(description = "결제 ID")
    private Long id;

    @Schema(description = "결제 금액")
    private int amount;

    @Schema(description = "결제 수단")
    private PaymentMethod paymentMethod;

    @Schema(description = "결제 상태 (PAID, CANCELLED)", example = "PAID")
    private PaymentStatus paymentStatus;

    @Schema(description = "결제 생성 일시")
    private LocalDateTime createdAt;

    @Schema(description = "취소 일시")
    private LocalDateTime cancelledAt;

    public PaymentResponse(Long id, int amount, PaymentMethod paymentMethod, PaymentStatus paymentStatus, LocalDateTime createdAt, LocalDateTime cancelledAt) {
        this.id = id;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.createdAt = createdAt;
        this.cancelledAt = cancelledAt;
    }

    public Long getId() {
        return id;
    }

    public int getAmount() {
        return amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }
}
