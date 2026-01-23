package com.byunsum.ticket_reservation.reservation.dto;

import java.time.LocalDateTime;

public record PreReservationResponse(
        Long id,
        Long performanceRoundId,
        String performanceTitle,
        String status,
        LocalDateTime appliedAt
) {
}
