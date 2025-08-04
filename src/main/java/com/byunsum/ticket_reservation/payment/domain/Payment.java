package com.byunsum.ticket_reservation.payment.domain;

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

    public void markAsPaid() {
        this.status = PaymentStatus.PAID;
    }


    public void markAsCancelled() {
        this.status = PaymentStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
    }
}
