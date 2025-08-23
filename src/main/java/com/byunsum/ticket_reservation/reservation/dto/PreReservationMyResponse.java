package com.byunsum.ticket_reservation.reservation.dto;

import java.time.LocalDateTime;

public record PreReservationMyResponse(
        Long performanceId,
        String performanceTitle,
        String status,
        LocalDateTime appliedAt
) {
}
