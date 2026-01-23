package com.byunsum.ticket_reservation.admin.controller;

import com.byunsum.ticket_reservation.reservation.dto.pre.PreReservationAdminResponse;
import com.byunsum.ticket_reservation.reservation.service.PreReservationService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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
            @RequestParam Long performanceId
    ) {
        preReservationService.drawWinners(performanceId);
        return ResponseEntity.ok("추첨 완료");
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "공연별 선예매 응모자 조회", description = "관리자가 특정 공연에 대한 응모자 전체를 조회합니다.")
    public ResponseEntity<List<PreReservationAdminResponse>> getPreReservations(
            @RequestParam Long performanceId
    ) {
        return ResponseEntity.ok(preReservationService.getPreReservationsByPerformance(performanceId));
    }
}
