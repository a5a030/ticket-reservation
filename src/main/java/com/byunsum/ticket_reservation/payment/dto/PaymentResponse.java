package com.byunsum.ticket_reservation.payment.dto;

import com.byunsum.ticket_reservation.payment.domain.PaymentCancelReason;
import com.byunsum.ticket_reservation.payment.domain.PaymentMethod;
import com.byunsum.ticket_reservation.payment.domain.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class PaymentResponse {
    @Schema(description = "결제 ID")
    private Long id;

    @Schema(description = "총 결제 금액")
    private int amount;

    @Schema(description = "부분 취소된 금액 (없으면 null)")
    private Integer partialAmount;

    @Schema(description = "취소 수수료 (없으면 null)")
    private Integer cancelFee;

    @Schema(description = "실제 환불된 금액 (없으면 null)")
    private Integer refundAmount;

    @Schema(description = "결제 수단")
    private PaymentMethod paymentMethod;

    @Schema(description = "결제 상태 (PAID, CANCELLED, PARTIAL_CANCELLED)", example = "PAID")
    private PaymentStatus paymentStatus;

    @Schema(description = "결제 생성 일시")
    private LocalDateTime createdAt;

    @Schema(description = "취소 일시")
    private LocalDateTime cancelledAt;

    @Schema(description = "취소 사유 (없으면 null)")
    private PaymentCancelReason cancelReason;

    @Schema(description = "계좌번호")
    private String accountNumber;

    public PaymentResponse(Long id, int amount, Integer partialAmount, Integer cancelFee, Integer refundAmount, PaymentMethod paymentMethod, PaymentStatus paymentStatus, LocalDateTime createdAt, LocalDateTime cancelledAt, PaymentCancelReason cancelReason, String accountNumber) {
        this.id = id;
        this.amount = amount;
        this.partialAmount = partialAmount;
        this.cancelFee = cancelFee;
        this.refundAmount = refundAmount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.createdAt = createdAt;
        this.cancelledAt = cancelledAt;
        this.cancelReason = cancelReason;
        this.accountNumber = accountNumber;
    }

    public Long getId() {
        return id;
    }

    public int getAmount() {
        return amount;
    }

    public Integer getPartialAmount() {
        return partialAmount;
    }

    public Integer getCancelFee() {
        return cancelFee;
    }

    public Integer getRefundAmount() {
        return refundAmount;
    }

    public PaymentCancelReason getCancelReason() {
        return cancelReason;
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

    public String getAccountNumber() {
        return accountNumber;
    }
}
