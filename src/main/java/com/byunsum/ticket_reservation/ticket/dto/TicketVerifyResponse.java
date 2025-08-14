package com.byunsum.ticket_reservation.ticket.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record TicketVerifyResponse(
        @Schema(description = "검증 성공 여부", example = "true")
        boolean success,

        @Schema(description = "티켓 상태", example = "USED")
        String status,

        @Schema(description = "결과 메시지", example = "입장 완료")
        String message) {
}
