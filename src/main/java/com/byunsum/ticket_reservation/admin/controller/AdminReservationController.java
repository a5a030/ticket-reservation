package com.byunsum.ticket_reservation.admin.controller;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.reservation.domain.pre.Reservation;
import com.byunsum.ticket_reservation.reservation.repository.ReservationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/reservations")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "관리자 예매 API", description = "예매 관리 및 배송 처리")
public class AdminReservationController {
    private final ReservationRepository reservationRepository;

    public AdminReservationController(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Operation(summary = "배송 시작 처리", description = "해당 예약 건을 배송 시작 상태로 변경합니다.")
    @PutMapping("/{id}/ship")
    public ResponseEntity<Void> startShipping(@PathVariable Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        if(reservation.isShipped()) {
            throw new CustomException(ErrorCode.DELIVERY_ALREADY_STARTED);
        }

        reservation.markAsShipped();
        reservationRepository.save(reservation);

        return ResponseEntity.noContent().build();
    }
}
