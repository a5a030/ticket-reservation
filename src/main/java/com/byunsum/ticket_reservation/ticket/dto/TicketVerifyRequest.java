package com.byunsum.ticket_reservation.ticket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record TicketVerifyRequest(
        @NotBlank(message = "티켓 코드는 필수입니다.")
        @Schema(description = "검증할 티켓 코드", example = "550e8400-e29b-41d4-a716-446655440000")
        String ticketCode
) {
}
