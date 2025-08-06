package com.byunsum.ticket_reservation.review.controller;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.review.dto.ReviewRequest;
import com.byunsum.ticket_reservation.review.dto.ReviewResponse;
import com.byunsum.ticket_reservation.review.dto.ReviewStatisticsResponse;
import com.byunsum.ticket_reservation.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "리뷰 API", description = "공연 후기 작성 및 조회 API")
@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Operation(summary = "리뷰 등록", description = "공연에 대한 후기를 작성합니다.")
    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(@RequestBody ReviewRequest request, @AuthenticationPrincipal Member member) {
        ReviewResponse response = reviewService.createReview(request, member);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "공연별 리뷰 조회", description = "특정 공연에 등록된 모든 리뷰를 조회합니다.")
    @GetMapping("/by-performance/{performanceId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByPerformance(@PathVariable Long performanceId,
                                                                        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(reviewService.getReviewsByPerformance(performanceId, pageable));
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "내가 쓴 리뷰 조회", description = "사용자가 작성한 모든 후기를 조회합니다.")
    @GetMapping("/my")
    public ResponseEntity<List<ReviewResponse>> getMyReviews(@AuthenticationPrincipal Member member) {
        if(member == null){
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        List<ReviewResponse> responses = reviewService.getReviewsByMember(member.getId());
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "공연별 감정 통계", description = "공연에 대한 전체 리뷰 감정 분포 및 평점 평균을 반환합니다.")
    @GetMapping("/statistics/{performanceId}")
    public ResponseEntity<ReviewStatisticsResponse> getStatistics(@PathVariable Long performanceId) {
        ReviewStatisticsResponse response = reviewService.getReviewStatistics(performanceId);

        return ResponseEntity.ok(response);
    }
}
