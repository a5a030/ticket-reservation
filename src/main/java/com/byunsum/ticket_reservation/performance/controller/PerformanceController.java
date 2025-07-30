package com.byunsum.ticket_reservation.performance.controller;

import com.byunsum.ticket_reservation.performance.dto.PerformanceRequest;
import com.byunsum.ticket_reservation.performance.dto.PerformanceResponse;
import com.byunsum.ticket_reservation.performance.service.PerformanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "공연 API", description = "공연 등록 및 조회 관련 API")
@RestController
@RequestMapping("/admin/performances")
public class PerformanceController {
    private final PerformanceService performanceService;

    public PerformanceController(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    @Operation(summary = "공연 등록 (관리자)", description = "공연 정보를 등록합니다. (관리자 전용)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "공연 등록 성공"),
            @ApiResponse(responseCode = "400", description = "입력 값 오류")
    })
    @PostMapping
    public ResponseEntity<Void> savePerformance(@RequestBody PerformanceRequest request) {
        performanceService.createPerformance(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "전체 공연 목록 조회", description = "사용자에게 공개되는 공연 전체 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공연 목록 조회 성공")
    })
    @GetMapping("/public")
    public ResponseEntity<List<PerformanceResponse>> getAllPerformance() {
        List<PerformanceResponse> performances = performanceService.getAllPerformances();
        return ResponseEntity.ok(performances);
    }

    @Operation(summary = "공연 상세 조회", description = "공연 ID를 통해 공연 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공연 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 공연 없음")
    })
    @GetMapping("/public/{id}")
    public ResponseEntity<PerformanceResponse> getPerformanceById(@PathVariable Long id) {
        PerformanceResponse response = performanceService.getPerformanceById(id);
        return ResponseEntity.ok(response);
    }
}
