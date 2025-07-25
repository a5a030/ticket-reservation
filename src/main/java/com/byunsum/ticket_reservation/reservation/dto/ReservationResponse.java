package com.byunsum.ticket_reservation.reservation.dto;

import java.time.LocalDateTime;

public class ReservationResponse {
    private String reservationCode;
    private String seatNo;
    private int price;
    private LocalDateTime reservedAt;

    public ReservationResponse(String reservationCode, String seatNo, int price, LocalDateTime reservedAt) {
        this.reservationCode = reservationCode;
        this.seatNo  = seatNo;
        this.price = price;
        this.reservedAt = reservedAt;
    }

    public void setReservationCode(String reservationCode) {
        this.reservationCode = reservationCode;
    }

    public String getReservationCode() {
        return reservationCode;
    }

    public String getSeatNo() {
        return seatNo;
    }

    public int getPrice() {
        return price;
    }

    public LocalDateTime getReservedAt() {
        return reservedAt;
    }
}
