package com.byunsum.ticket_reservation.payment.dto;

import com.byunsum.ticket_reservation.payment.domain.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;

public class PaymentRequest {
    @Schema(description = "결제 수단")
    private PaymentMethod paymentMethod;

    @Schema(description = "연결된 예매 ID", example = "1")
    private Long reservationId;

    public PaymentRequest() {
    }

    public PaymentRequest(PaymentMethod paymentMethod, Long reservationId) {
        this.paymentMethod = paymentMethod;
        this.reservationId = reservationId;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public Long getReservationId() {
        return reservationId;
    }
}
