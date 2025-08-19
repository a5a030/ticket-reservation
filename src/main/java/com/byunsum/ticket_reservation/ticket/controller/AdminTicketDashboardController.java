package com.byunsum.ticket_reservation.ticket.controller;

import com.byunsum.ticket_reservation.ticket.dto.VerificationStatsResponse;
import com.byunsum.ticket_reservation.ticket.service.TicketDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/admin/tickets")
@PreAuthorize("hasRole('ADMIN')")
public class AdminTicketDashboardController {
    private final TicketDashboardService ticketDashboardService;

    public AdminTicketDashboardController(TicketDashboardService ticketDashboardService) {
        this.ticketDashboardService = ticketDashboardService;
    }

    @Operation(summary = "티켓 검증 통계 조회", description = "기간별 검증 성공률, 상태별 카운트, 시간대별 카운트를 반환합니다.")
    @GetMapping("/stats")
    public ResponseEntity<VerificationStatsResponse> getStats(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
                                                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(ticketDashboardService.getStats(start, end));
    }
}
