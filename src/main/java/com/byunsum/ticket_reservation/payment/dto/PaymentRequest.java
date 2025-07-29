package com.byunsum.ticket_reservation.payment.dto;

import com.byunsum.ticket_reservation.payment.domain.PaymentMethod;

public class PaymentRequest {
    private int amount;
    private PaymentMethod paymentMethod;
    private Long reservationId;

    public PaymentRequest() {
    }

    public PaymentRequest(int amount, PaymentMethod paymentMethod, Long reservationId) {
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.reservationId = reservationId;
    }

    public int getAmount() {
        return amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public Long getReservationId() {
        return reservationId;
    }
}
