package com.byunsum.ticket_reservation.payment.dto;

import java.math.BigDecimal;

public interface PaymentStatistics {
    String getPaymentMethod();
    Long getCount();
    BigDecimal getTotal();
}
