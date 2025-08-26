package com.byunsum.ticket_reservation.reservation.dto;

import com.byunsum.ticket_reservation.reservation.domain.Reservation;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class ReservationResponse {
    @Schema(description = "예매 ID", example = "1")
    private Long id;

    @Schema(description = "예매 고유 코드", example = "ABC123XYZ")
    private String reservationCode;

    @Schema(description = "공연 제목", example = "뮤지컬 햄릿")
    private String performanceTitle;

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

    @Schema(description = "재확정 가능 TTL (초 단위)", example = "280")
    private Long ttlSeconds;

    public ReservationResponse(Reservation reservation, Long ttlSeconds) {
        this.id = reservation.getId();
        this.reservationCode = reservationCode;
        this.performanceTitle = performanceTitle;
        this.seatNo  = seatNo;
        this.price = price;
        this.reservedAt = reservedAt;
        this.status = status;
        this.reconfirmed = reconfirmed;
        this.ttlSeconds = ttlSeconds;
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

    public Long getId() {
        return id;
    }

    public String getPerformanceTitle() {
        return performanceTitle;
    }

    public Long getTtlSeconds() {
        return ttlSeconds;
    }
}
