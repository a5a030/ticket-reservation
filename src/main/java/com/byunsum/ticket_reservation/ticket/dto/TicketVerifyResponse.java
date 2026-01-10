package com.byunsum.ticket_reservation.ticket.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record TicketVerifyResponse(
        @Schema(description = "검증 성공 여부", example = "true")
        boolean success,

        @Schema(description = "티켓 상태", example = "USED")
        String status,

        @Schema(description = "결과 메시지", example = "입장 완료")
        String message,

        @Schema(description = "공연명")
        String performanceTitle,

        @Schema(description = "좌석 번호", example = "A23")
        String seatNo,

        @Schema(description = "티켓 만료시각", example = "2025-08-19T18:30:00")
        LocalDateTime expiresAt,

        @Schema(description = "검증자 식별자(스태프 loginId 또는 시스템 ID)")
        String verifier,

        @Schema(description = "검증 요청자 IP/Device 정보")
        String deviceInfo,

        @Schema(description = "검증 시각")
        LocalDateTime verifiedAt) {

}
