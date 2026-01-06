package com.byunsum.ticket_reservation.admin.controller;

import com.byunsum.ticket_reservation.performance.dto.PerformanceRequest;
import com.byunsum.ticket_reservation.performance.dto.PerformanceSummaryResponse;
import com.byunsum.ticket_reservation.performance.service.PerformanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "공연 API(관리자)", description = "관리자 전용 공연 등록/관리 API")
@RestController
@RequestMapping("/admin/performances")
public class AdminPerformanceController {
    private final PerformanceService performanceService;

    public AdminPerformanceController(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    @Operation(summary = "공연 등록", description = "공연 정보를 등록합니다. (관리자 전용)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "공연 등록 성공"),
            @ApiResponse(responseCode = "400", description = "입력 값 오류"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Long> savePerformance(@Valid @RequestBody PerformanceRequest request) {
        Long id = performanceService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @Operation(summary = "공연 수정", description = "관리자가 공연 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공연 수정 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 오류"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "해당 공연 없음")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<PerformanceSummaryResponse> updatePerformance(@PathVariable Long id, @Valid @RequestBody PerformanceRequest request) {
        PerformanceSummaryResponse response = performanceService.update(id, request);
        return ResponseEntity.ok(response);
    }
}
