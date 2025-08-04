package com.byunsum.ticket_reservation.payment.dto;

public interface PaymentStatistics {
    String getPaymentMethod();
    Long getCount();
    Long getTotal();
}
