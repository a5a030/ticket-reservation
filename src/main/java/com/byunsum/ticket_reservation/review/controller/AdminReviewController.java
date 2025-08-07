package com.byunsum.ticket_reservation.review.controller;

import com.byunsum.ticket_reservation.review.dto.ReviewDashboardResponse;
import com.byunsum.ticket_reservation.review.service.ReviewAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/review-dashboard")
@Tag(name = "관리자 후기 대시보드 API", description = "공연별 후기 통계 및 요약 정보를 제공합니다.")
public class AdminReviewController {
    private ReviewAdminService reviewAdminService;

    public AdminReviewController(ReviewAdminService reviewAdminService) {
        this.reviewAdminService = reviewAdminService;
    }

    @GetMapping("/{performanceId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "공연 후기 대시보드 조회", description = "공연별 감정 통계, 평균 평점, 키워드, 리뷰 예시 등을 반환합니다.")
    public ReviewDashboardResponse getDashboard(@PathVariable Long performanceId) {
        return reviewAdminService.getDashboard(performanceId);
    }
}
