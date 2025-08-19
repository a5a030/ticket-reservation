package com.byunsum.ticket_reservation.ticket.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin/dashboard/stub")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "관리자 대시보드 Stub", description = "프론트 연동 전용 관리자 Stub API")
public class AdminDashboardStubController {
    @Operation(summary = "Stub: 판매 현황", description = "총 매출액, 예매 수, 취소 건수 반환")
    @GetMapping("/sales")
    public ResponseEntity<Map<String, Object>> getSalesStub() {
        return ResponseEntity.ok(Map.of(
                "totalSales", 1234567,
                "totalReservations", 3421,
                "canceledReservations", 45
        ));
    }

    @GetMapping("/reviews")
    @Operation(summary = "Stub: 리뷰 통계", description = "긍/부정 비율 및 인기 키워드 반환")
    public ResponseEntity<Map<String, Object>> getReviewsStub() {
        return ResponseEntity.ok(Map.of(
                "positive", 78,
                "negative", 22,
                "topKeywords", new String[]{"재밌다", "감동", "좌석좋음"}
        ));
    }

    @GetMapping("/tickets")
    @Operation(summary = "Stub: 티켓 검증 현황", description = "검증 성공/실패/블랙리스트 건수 반환")
    public ResponseEntity<Map<String, Object>> getTicketStatsStub() {
        return ResponseEntity.ok(Map.of(
                "verified", 1024,
                "rejected", 56,
                "blacklisted", 12
        ));
    }
}