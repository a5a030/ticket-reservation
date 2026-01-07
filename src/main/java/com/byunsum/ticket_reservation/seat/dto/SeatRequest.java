package com.byunsum.ticket_reservation.seat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SeatRequest {
    @NotBlank
    @Schema(description = "좌석 번호", example = "A10")
    private String seatNo;

    @Min(0)
    @Schema(description = "좌석 가격")
    private int price;

    @NotNull
    @Schema(description = "연결할 공연 회차ID", example = "1")
    private Long performanceRoundId;

    public SeatRequest() {
    }

    public String getSeatNo() {
        return seatNo;
    }

    public void setSeatNo(String seatNo) {
        this.seatNo = seatNo;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public Long getPerformanceRoundId() {
        return performanceRoundId;
    }

    public void setPerformanceRoundId(Long performanceRoundId) {
        this.performanceRoundId = performanceRoundId;
    }
}
