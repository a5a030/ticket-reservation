package com.byunsum.ticket_reservation.payment.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class RefundHistory {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    private BigDecimal cancelFee;
    private BigDecimal refundAmount;
    private LocalDateTime createdAt =  LocalDateTime.now();

    public RefundHistory() {
    }

    public RefundHistory(Payment payment, BigDecimal cancelFee, BigDecimal refundAmount) {
        this.payment = payment;
        this.cancelFee = cancelFee;
        this.refundAmount = refundAmount;
    }

    public Long getId() {
        return id;
    }

    public Payment getPayment() {
        return payment;
    }

    public BigDecimal getCancelFee() {
        return cancelFee;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
