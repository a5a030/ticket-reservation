package com.byunsum.ticket_reservation.reservation.dto.pre;

import java.time.LocalDateTime;

public record SeatAssignmentMyResponse(
        Long performanceId,
        String performanceTitle,
        String preReservationStatus,
        Long assignmentId,
        String seatNo,
        String assignmentStatus,
        LocalDateTime expiresAt
) {
}
