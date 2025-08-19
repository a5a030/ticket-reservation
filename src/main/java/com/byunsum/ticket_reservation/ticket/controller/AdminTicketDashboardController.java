package com.byunsum.ticket_reservation.ticket.controller;

import com.byunsum.ticket_reservation.ticket.dto.VerificationStatsResponse;
import com.byunsum.ticket_reservation.ticket.service.TicketDashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/admin/dashboard/tickets")
@PreAuthorize("hasRole('ADMIN')")
public class AdminTicketDashboardController {
    private final TicketDashboardService ticketDashboardService;

    public AdminTicketDashboardController(TicketDashboardService ticketDashboardService) {
        this.ticketDashboardService = ticketDashboardService;
    }

    @GetMapping("/stats")
    public ResponseEntity<VerificationStatsResponse> getStats(@RequestParam LocalDateTime start,
                                                              @RequestParam LocalDateTime end) {
        return ResponseEntity.ok(ticketDashboardService.getStats(start, end));
    }
}
