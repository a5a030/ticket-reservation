package com.byunsum.ticket_reservation.performance.controller;

import com.byunsum.ticket_reservation.performance.dto.PerformanceRequest;
import com.byunsum.ticket_reservation.performance.dto.PerformanceResponse;
import com.byunsum.ticket_reservation.performance.service.PerformanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "공연 API(사용자)", description = "사용자용 공연 조회 API")
@RestController
@RequestMapping("/performances")
public class PerformanceController {
    private final PerformanceService performanceService;

    public PerformanceController(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    @Operation(summary = "전체 공연 목록 조회", description = "사용자에게 공개되는 공연 전체 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공연 목록 조회 성공")
    })
    @GetMapping()
    public ResponseEntity<List<PerformanceResponse>> getAllPerformance() {
        List<PerformanceResponse> performances = performanceService.getAllPerformances();
        return ResponseEntity.ok(performances);
    }

    @Operation(summary = "공연 상세 조회", description = "공연 ID를 통해 공연 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공연 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 공연 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PerformanceResponse> getPerformanceById(@PathVariable Long id) {
        PerformanceResponse response = performanceService.getPerformanceById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "공연 목록 조회 (정렬 옵션 + 페이징)",
            description = "정렬 기준에 따라 공연 목록을 조회합니다. (all=전체, imminent=임박순, popular=인기순)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공연 목록 조회 성공")
    })
    @GetMapping("/sorted")
    public ResponseEntity<Page<PerformanceResponse>> getPerformancesSorted(@RequestParam(defaultValue = "all") String sort, Pageable pageable) {
        Page<PerformanceResponse> performances = performanceService.getPerformanceSorted(sort, pageable);

        return ResponseEntity.ok(performances);
    }
}
