package com.byunsum.ticket_reservation.ticket.controller;

import com.byunsum.ticket_reservation.ticket.dto.VerificationStatsResponse;
import com.byunsum.ticket_reservation.ticket.service.TicketDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "관리자 대시보드 - 티켓 검증 통계", description = "티켓 검증 로그 기반 통계 조회 API")
public class AdminTicketDashboardController {
    private final TicketDashboardService ticketDashboardService;

    public AdminTicketDashboardController(TicketDashboardService ticketDashboardService) {
        this.ticketDashboardService = ticketDashboardService;
    }

    @Operation(
            summary = "티켓 검증 통계 조회",
            description = "TicketVerificationLog 기반으로 기간 내 검증 성공률, 결과별 카운트, 시간대별 분포를 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "통계 조회 성공",
                            content = @Content(schema = @Schema(implementation = VerificationStatsResponse.class))
                    ),
                    @ApiResponse(responseCode = "401", description = "인증 실패 (관리자 권한 필요)"),
                    @ApiResponse(responseCode = "403", description = "인가 실패 (관리자 권한 없음)")
            }
    )
    @GetMapping("/stats")
    public ResponseEntity<VerificationStatsResponse> getStats(@Parameter(description = "조회 시작 시간 (yyyy-MM-dd'T'HH:mm:ss)", example = "2025-08-01T00:00:00") @RequestParam LocalDateTime start,
                                                              @Parameter(description = "조회 종료 시간 (yyyy-MM-dd'T'HH:mm:ss)", example = "2025-08-19T23:59:59") @RequestParam LocalDateTime end
    ) {
        return ResponseEntity.ok(ticketDashboardService.getStats(start, end));
    }
}
