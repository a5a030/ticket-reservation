package com.byunsum.ticket_reservation.admin.controller;

import com.byunsum.ticket_reservation.admin.dto.ReviewStatsResponse;
import com.byunsum.ticket_reservation.admin.dto.SalesStatsResponse;
import com.byunsum.ticket_reservation.admin.service.AdminDashboardService;
import com.byunsum.ticket_reservation.ticket.dto.VerificationStatsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/admin/dashboard")
@Tag(name = "Admin 대시보드", description = "관리자 대시보드 통계 API")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {
    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(AdminDashboardService adminDashboardService) {
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping("/sales")
    @Operation(summary = "매출 통계 조회", description = "결제 내역 기반 매출 통계를 조회합니다.")
    public ResponseEntity<SalesStatsResponse> getSalesStats() {
        return ResponseEntity.ok(adminDashboardService.getSalesStats());
    }

    @GetMapping("/review")
    @Operation(summary = "리뷰 통계 조회", description = "리뷰 요약 및 키워드 통계를 조회합니다.")
    public ResponseEntity<ReviewStatsResponse> getReviewStats() {
        return ResponseEntity.ok(adminDashboardService.getReviewStats());
    }

    @GetMapping("/tickets")
    @Operation(summary = "티켓 검증 통계 조회", description = "기간 내 검표 로그 기반 통계를 조회합니다.")
    public ResponseEntity<VerificationStatsResponse> getTicketStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return ResponseEntity.ok(adminDashboardService.getTicketStats(start, end));
    }
}
