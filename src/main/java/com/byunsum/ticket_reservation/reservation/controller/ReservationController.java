package com.byunsum.ticket_reservation.reservation.controller;

import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.reservation.dto.ReservationRequest;
import com.byunsum.ticket_reservation.reservation.dto.ReservationResponse;
import com.byunsum.ticket_reservation.reservation.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> reservation(@RequestBody ReservationRequest request, @AuthenticationPrincipal Member member) {
        ReservationResponse response = reservationService.createReservation(request, member);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{code}")
    public ResponseEntity<ReservationResponse> getReservation(@PathVariable String code) {
        ReservationResponse response = reservationService.getReservationByCode(code);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/confirm")
    public ResponseEntity<ReservationResponse> confirmReservation(@RequestParam Long performanceId, @RequestParam Long seatId, @AuthenticationPrincipal Member member) {
        ReservationResponse response = reservationService.confirmReservation(performanceId, seatId, member);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{code}/cancel")
    public ResponseEntity<Void> cancelReservation(@PathVariable String code, @AuthenticationPrincipal Member member) {
        reservationService.cancelReservation(code, member.getId());

        return ResponseEntity.ok().build();
    }
}
