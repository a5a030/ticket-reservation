package com.byunsum.ticket_reservation.reservation.dto.pre;

import java.time.LocalDateTime;

public record PreReservationMyResponse(
        Long performanceRoundId,
        Long performanceId,
        String performanceTitle,
        LocalDateTime reservationStartTime,
        String status,
        LocalDateTime appliedAt
) {
}
