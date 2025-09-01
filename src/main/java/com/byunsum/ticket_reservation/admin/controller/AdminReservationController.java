package com.byunsum.ticket_reservation.admin.controller;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.reservation.domain.Reservation;
import com.byunsum.ticket_reservation.reservation.repository.ReservationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @PostMapping("/{id}/ship")
    public String startShipping(@PathVariable Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        if(reservation.isShipped()) {
            throw new CustomException(ErrorCode.CANCEL_NOT_ALLOWED_AFTER_SHIPMENT);
        }

        reservation.markAsShipped();
        reservationRepository.save(reservation);

        return "예약 ID " + id + "의 배송이 시작되었습니다.";
    }
}
