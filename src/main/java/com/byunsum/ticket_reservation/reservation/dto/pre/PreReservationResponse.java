package com.byunsum.ticket_reservation.reservation.dto.pre;

import java.time.LocalDateTime;

public record PreReservationResponse(
        Long id,
        Long performanceId,
        String performanceTitle,
        String type,
        String status,
        LocalDateTime appliedAt
) {
}
