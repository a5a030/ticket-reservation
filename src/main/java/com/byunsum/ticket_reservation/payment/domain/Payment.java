package com.byunsum.ticket_reservation.payment.domain;

import com.byunsum.ticket_reservation.reservation.domain.Reservation;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int amount;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private LocalDateTime createdAt;

    @OneToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    private LocalDateTime cancelledAt;

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

    public void markAsPaid() {
        this.status = PaymentStatus.PAID;
    }


    public void markAsCancelled() {
        this.status = PaymentStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
    }
}
