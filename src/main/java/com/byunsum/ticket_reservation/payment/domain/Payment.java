package com.byunsum.ticket_reservation.payment.domain;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.reservation.domain.Reservation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "결제 ID")
    private Long id;

    @Schema(description = "결제 금액")
    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "결제 수단", example = "BANK_TRANSFER")
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "결제 상태", example = "PENDING")
    private PaymentStatus status;

    @Schema(description = "결제일시")
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    @Schema(description = "연결된 예매 정보")
    private Reservation reservation;

    @Schema(description = "결제 취소 일시", nullable = true)
    private LocalDateTime cancelledAt;

    @Schema(description = "재확정일시")
    private LocalDateTime reconfirmedAt;

    @Schema(description = "계좌번호")
    @Column(name = "account_number")
    private String accountNumber;

    @Schema(description = "취소 수수료")
    @Column(nullable = false)
    private BigDecimal cancelFee = BigDecimal.ZERO;

    @Schema(description = "실제 환불금액")
    @Column(nullable = false)
    private BigDecimal refundAmount =  BigDecimal.ZERO;

    @Schema(description = "부분 취소된 누적 금액")
    @Column(nullable = false)
    private BigDecimal partialAmount = BigDecimal.ZERO;

    @Schema(description = "취소사유")
    @Enumerated(EnumType.STRING)
    private PaymentCancelReason cancelReason;

    private boolean isPaidOrPartialCancelled()  {
        return this.status == PaymentStatus.PAID || this.status == PaymentStatus.PARTIAL_CANCELLED;
    }

    public BigDecimal getCancelFee() {
        return cancelFee;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    //부분취소는 여러번 가능
    private boolean isCancelled() {
        return this.status == PaymentStatus.CANCELLED;
    }

    public Payment() {
    }

    public Payment(BigDecimal amount, PaymentMethod paymentMethod, PaymentStatus status, Reservation reservation) {
        if(amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("amount must be >= 0");
        }

        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.reservation = reservation;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public LocalDateTime getReconfirmedAt() {
        return reconfirmedAt;
    }

    public BigDecimal getPartialAmount() {
        return partialAmount;
    }

    public PaymentCancelReason getCancelReason() {
        return cancelReason;
    }

    public void markAsPaid() {
        if(this.status != PaymentStatus.PENDING) {
            throw new CustomException(ErrorCode.INVALID_PAYMENT_STATUS);
        }

        this.status = PaymentStatus.PAID;
    }

    //paid/partial_canclled 취소
    public void markAsCancelled(BigDecimal cancelFee, BigDecimal refundAmount) {
        if(this.status != PaymentStatus.PAID && this.status != PaymentStatus.PARTIAL_CANCELLED) {
            throw new CustomException(ErrorCode.INVALID_PAYMENT_STATUS);
        }

        this.status = PaymentStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.cancelFee = (cancelFee == null ? BigDecimal.ZERO : cancelFee).max(BigDecimal.ZERO);
        this.refundAmount = ((refundAmount == null) ? BigDecimal.ZERO : refundAmount).max(BigDecimal.ZERO);
    }

    public void markAsCancelled(BigDecimal cancelFee, BigDecimal refundAmount, PaymentCancelReason reason) {
        markAsCancelled(cancelFee, refundAmount);
        this.cancelReason = reason;
    }

    //pending 취소
    public void cancel(PaymentCancelReason reason) {
        if(isCancelled()) {
            throw new CustomException(ErrorCode.ALREADY_CANCELED_PAYMENT);
        }

        if(this.status != PaymentStatus.PENDING) {
            throw new CustomException(ErrorCode.INVALID_PAYMENT_STATUS);
        }

        this.status = PaymentStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.cancelFee = BigDecimal.ZERO;
        this.refundAmount = BigDecimal.ZERO;
        this.cancelReason = reason;
    }

    public void markAsReconfirmed() {
        if(this.status != PaymentStatus.CANCELLED) {
            throw new CustomException(ErrorCode.INVALID_PAYMENT_STATUS);
        }

        this.cancelFee = BigDecimal.ZERO;
        this.refundAmount = BigDecimal.ZERO;
        this.cancelReason = null;
        this.partialAmount = BigDecimal.ZERO;
        this.reconfirmedAt = LocalDateTime.now();
        this.status = PaymentStatus.PAID;
        this.cancelledAt = null;
    }

    public void cancelPartial(BigDecimal cancelAmount, BigDecimal cancelFee, PaymentCancelReason reason) {
        if(isCancelled()) {
            throw new CustomException(ErrorCode.ALREADY_CANCELED_PAYMENT);
        }

        if(!isPaidOrPartialCancelled()) {
            throw new CustomException(ErrorCode.INVALID_PAYMENT_STATUS);
        }

        if(reason == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        if(cancelAmount == null || cancelAmount.compareTo(BigDecimal.ZERO) <= 0 || cancelAmount.compareTo(this.amount) > 0) {
            throw new CustomException(ErrorCode.INVALID_PAYMENT_AMOUNT);
        }

        if(cancelFee == null || cancelFee.compareTo(BigDecimal.ZERO) < 0) {
            throw new CustomException(ErrorCode.INVALID_PAYMENT_AMOUNT);
        }

        BigDecimal newPartialAmount = this.partialAmount.add(cancelAmount);
        if(newPartialAmount.compareTo(this.amount) > 0) {
            throw new CustomException(ErrorCode.INVALID_PAYMENT_AMOUNT);
        }

        this.partialAmount = newPartialAmount;
        this.cancelFee = this.cancelFee.add(cancelFee);
        this.cancelReason = reason;
        this.cancelledAt = LocalDateTime.now();

        if(this.partialAmount.compareTo(this.amount) == 0) {
            this.status = PaymentStatus.CANCELLED;

            BigDecimal calculated = this.amount.subtract(this.cancelFee);
            this.refundAmount = calculated.max(BigDecimal.ZERO);

            return;
        }

        BigDecimal calculated = this.amount
            .subtract(this.partialAmount)
            .subtract(this.cancelFee);

        this.refundAmount = calculated.max(BigDecimal.ZERO);
        this.status = PaymentStatus.PARTIAL_CANCELLED;

    }
}
