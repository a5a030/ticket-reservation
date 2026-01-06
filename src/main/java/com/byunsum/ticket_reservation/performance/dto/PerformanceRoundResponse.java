package com.byunsum.ticket_reservation.performance.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class PerformanceRoundResponse {
    @Schema(description = "회차 ID", example = "10")
    private Long id;

    @Schema(description = "공연 시작 일시")
    private LocalDateTime startDateTime;

    @Schema(description = "공연 종료 일시")
    private LocalDateTime endDateTime;

    @Schema(description = "입장 시작 일시")
    private LocalDateTime entryDateTime;

    @Schema(description = "회차 번호")
    private int roundNumber;

    public PerformanceRoundResponse() {
    }

    public PerformanceRoundResponse(Long id, LocalDateTime startDateTime, LocalDateTime endDateTime, LocalDateTime entryDateTime, int roundNumber) {
        this.id = id;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.entryDateTime = entryDateTime;
        this.roundNumber = roundNumber;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public LocalDateTime getEntryDateTime() {
        return entryDateTime;
    }

    public int getRoundNumber() {
        return roundNumber;
    }
}
