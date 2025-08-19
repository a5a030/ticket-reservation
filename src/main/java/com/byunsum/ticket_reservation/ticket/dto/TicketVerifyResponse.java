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
        String perpormanceTitle,

        @Schema(description = "좌석 번호", example = "A23")
        String seatNo,

        @Schema(description = "티켓 만료시각", example = "2025-08-19T18:30:00")
        LocalDateTime expiresAt) {
}
