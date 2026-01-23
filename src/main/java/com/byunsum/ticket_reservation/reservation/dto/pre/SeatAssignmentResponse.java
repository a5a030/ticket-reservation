package com.byunsum.ticket_reservation.reservation.dto.pre;

import java.time.LocalDateTime;

public record SeatAssignmentResponse(
        Long assignmentId,
        Long preReservationId,
        Long seatId,
        String seatNo,
        String status,
        LocalDateTime assignedAt,
        LocalDateTime expiresAt
) {
}
