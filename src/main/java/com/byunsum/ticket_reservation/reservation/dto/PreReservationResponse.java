package com.byunsum.ticket_reservation.reservation.dto;

import java.time.LocalDateTime;

public record PreReservationResponse(
        Long id,
        Long roundId,
        String status,
        LocalDateTime appliedAt
) {
}
