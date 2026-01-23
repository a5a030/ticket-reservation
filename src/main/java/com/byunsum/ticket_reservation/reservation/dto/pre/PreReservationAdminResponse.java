package com.byunsum.ticket_reservation.reservation.dto.pre;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class PreReservationAdminResponse {
    @Schema(description = "응모 ID")
    private Long id;

    @Schema(description = "회원 ID")
    private Long memberId;

    @Schema(description = "회원 이메일")
    private String email;

    @Schema(description = "공연 ID")
    private Long performanceId;

    @Schema(description = "응모 상태 (WAITING, WINNER, LOSER)")
    private String status;

    @Schema(description = "응모 일시")
    private LocalDateTime appliedAt;

    public PreReservationAdminResponse(Long id, Long memberId, String email, Long performanceId, String status, LocalDateTime appliedAt) {
        this.id = id;
        this.memberId = memberId;
        this.email = email;
        this.performanceId = performanceId;
        this.status = status;
        this.appliedAt = appliedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public String getEmail() {
        return email;
    }

    public Long getPerformanceId() {
        return performanceId;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }
}
