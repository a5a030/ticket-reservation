package com.byunsum.ticket_reservation.admin.controller;

import com.byunsum.ticket_reservation.reservation.service.PreReservationService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/pre-reservations")
public class AdminPreReservationController {
    private final PreReservationService preReservationService;

    public AdminPreReservationController(PreReservationService preReservationService) {
        this.preReservationService = preReservationService;
    }

    @PostMapping("/draw")
    @Operation(summary = "선예매 당첨자 추첨", description = "관리자가 공연 단위로 선예매 당첨자를 추첨합니다.")
    public ResponseEntity<String> drawWinners(
            @RequestParam Long performanceId,
            @RequestParam int winnerCount
    ) {
        preReservationService.drawWinners(performanceId, winnerCount);
        return ResponseEntity.ok("추첨 완료");
    }
}
