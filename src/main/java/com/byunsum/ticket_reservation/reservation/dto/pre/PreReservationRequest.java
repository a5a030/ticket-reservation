package com.byunsum.ticket_reservation.reservation.dto.pre;

import com.byunsum.ticket_reservation.reservation.domain.pre.PreReservationType;
import jakarta.validation.constraints.NotNull;

public record PreReservationRequest(
        @NotNull Long performanceId,
        @NotNull PreReservationType type
        ) {
}
