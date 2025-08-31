package com.byunsum.ticket_reservation.payment.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class RefundHistory {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    private int cancelFee;
    private int refundAmount;
    private LocalDateTime createdAt =  LocalDateTime.now();

    public RefundHistory() {
    }

    public RefundHistory(Payment payment, int cancelFee, int refundAmount) {
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

    public int getCancelFee() {
        return cancelFee;
    }

    public int getRefundAmount() {
        return refundAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
