package com.byunsum.ticket_reservation.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class ReservationResponse {
    @Schema(description = "예매 고유 코드", example = "ABC123XYZ")
    private String reservationCode;

    @Schema(description = "좌석 번호", example = "A10")
    private String seatNo;

    @Schema(description = "결제 가격")
    private int price;

    @Schema(description = "예매 완료 일시")
    private LocalDateTime reservedAt;

    @Schema(description = "예매 상태", example = "CONFIRMED")
    private String status;

    @Schema(description = "재확정 여부", example = "true")
    private boolean reconfirmed;

    public ReservationResponse(String reservationCode, String seatNo, int price, LocalDateTime reservedAt, String status, boolean reconfirmed) {
        this.reservationCode = reservationCode;
        this.seatNo  = seatNo;
        this.price = price;
        this.reservedAt = reservedAt;
        this.status = status;
        this.reconfirmed = reconfirmed;
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

    public String getStatus() {
        return status;
    }

    public boolean isReconfirmed() {
        return reconfirmed;
    }
}
