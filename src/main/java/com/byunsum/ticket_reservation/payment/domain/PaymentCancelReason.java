package com.byunsum.ticket_reservation.payment.domain;

public enum PaymentCancelReason {
    USER_REQUEST,
    BANK_TRANSFER_EXPIRED,
    PAYMENT_FAILURE,
    ADMIN_FORCE_CANCEL
}
