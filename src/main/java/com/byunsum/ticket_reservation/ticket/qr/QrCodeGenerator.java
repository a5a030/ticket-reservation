package com.byunsum.ticket_reservation.ticket.qr;

import com.byunsum.ticket_reservation.reservation.domain.Reservation;

public interface QrCodeGenerator {
    String generate(Reservation reservation);
}
