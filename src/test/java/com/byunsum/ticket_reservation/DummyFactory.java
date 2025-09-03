package com.byunsum.ticket_reservation;

import com.byunsum.ticket_reservation.reservation.domain.Reservation;
import com.byunsum.ticket_reservation.reservation.domain.ReservationSeat;
import com.byunsum.ticket_reservation.seat.domain.Seat;

public class DummyFactory {
    public static ReservationSeat dummyReservationSeat() {
        Reservation reservation = new Reservation();
        Seat seat = new Seat();
        seat.setSeatNo("A1");

        return new ReservationSeat(reservation, seat);
    }
}
