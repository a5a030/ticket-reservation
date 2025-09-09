package com.byunsum.ticket_reservation.payment.domain;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.reservation.domain.Reservation;
import com.byunsum.ticket_reservation.reservation.domain.ReservationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "결제 ID")
    private Long id;

    @Schema(description = "결제 금액")
    private int amount;

    @Enumerated(EnumType.STRING)
    @Schema(description = "결제 수단", example = "BANK_TRANSFER")
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Schema(description = "결제 상태", example = "PENDING")
    private PaymentStatus status;

    @Schema(description = "결제일시")
    private LocalDateTime createdAt;

    @OneToOne
    @JoinColumn(name = "reservation_id")
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
    private Integer cancelFee;

    @Schema(description = "실제 환불금액")
    private Integer refundAmount;

    @Schema(description = "부분 취소된 누적 금액")
    private Integer partialAmount = 0;

    @Schema(description = "취소사유")
    @Enumerated(EnumType.STRING)
    private PaymentCancelReason cancelReason;



    public Integer getCancelFee() {
        return cancelFee;
    }

    public void setCancelFee(Integer cancelFee) {
        this.cancelFee = cancelFee;
    }

    public Integer getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(Integer refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    private boolean isCancelled() {
        return this.status == PaymentStatus.CANCELLED;
    }

    public Payment() {
    }

    public Payment(int amount, PaymentMethod paymentMethod, PaymentStatus status, Reservation reservation) {
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.reservation = reservation;
        this.createdAt = LocalDateTime.now();
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

    public Integer getPartialAmount() {
        return partialAmount;
    }

    public PaymentCancelReason getCancelReason() {
        return cancelReason;
    }

    public void markAsPaid() {
        this.status = PaymentStatus.PAID;
    }

    public void markAsCancelled(int cancelFee, int refundAmount) {
        this.status = PaymentStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.cancelFee = cancelFee;
        this.refundAmount = refundAmount;

        if(this.reservation != null) {
            this.reservation.cancel();
        }
    }

    public void cancel(PaymentCancelReason reason) {
        if(isCancelled()) {
            throw new CustomException(ErrorCode.ALREADY_CANCELED_PAYMENT);
        }

        this.status = PaymentStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.cancelFee = 0;
        this.refundAmount = 0;
        this.cancelReason = reason;

        if(this.reservation != null) {
            this.reservation.cancel();
        }
    }

    public void markAsReconfirmed() {
        if(this.status != PaymentStatus.CANCELLED) {
            throw new CustomException(ErrorCode.INVALID_PAYMENT_STATUS);
        }

        this.reconfirmedAt = LocalDateTime.now();
        this.status = PaymentStatus.PAID;
        this.cancelledAt = null;
    }

    public void cancelPartial(int cancelAmount, int cancelFee, PaymentCancelReason reason) {
        if(isCancelled()) {
            throw new CustomException(ErrorCode.ALREADY_CANCELED_PAYMENT);
        }

        if(cancelAmount <= 0 || cancelAmount > this.amount) {
            throw new CustomException(ErrorCode.INVALID_PAYMENT_AMOUNT);
        }

        this.partialAmount += cancelAmount;
        this.cancelFee = (this.cancelFee == null ? 0 : this.cancelFee) + cancelFee;
        this.cancelReason = reason;
        this.cancelledAt = LocalDateTime.now();

        if(this.partialAmount >= this.amount) {
            this.status = PaymentStatus.CANCELLED;
            this.refundAmount = 0;

            if(this.reservation != null) {
                this.reservation.cancel();
            }
        } else {
            this.status = PaymentStatus.PARTIAL_CANCELLED;
            this.refundAmount = this.amount - this.partialAmount - this.cancelFee;
        }
    }
}
