package com.byunsum.ticket_reservation.reservation.dto;

import java.time.LocalDateTime;

public record PreReservationResponse(
        Long id,
        Long performanceId,
        String status,
        LocalDateTime appliedAt
) {
}
