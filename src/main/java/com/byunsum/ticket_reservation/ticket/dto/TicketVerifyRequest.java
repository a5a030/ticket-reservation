package com.byunsum.ticket_reservation.ticket.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record TicketVerifyRequest(
        @Schema(description = "검증할 티켓 코드", example = "f81s9-d83sd-...")
        String ticketCode
) {
}
