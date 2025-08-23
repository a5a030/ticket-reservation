package com.byunsum.ticket_reservation.admin.controller;

import com.byunsum.ticket_reservation.performance.dto.PerformanceRequest;
import com.byunsum.ticket_reservation.performance.service.PerformanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            @ApiResponse(responseCode = "400", description = "입력 값 오류")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Void> savePerformance(@RequestBody PerformanceRequest request) {
        performanceService.createPerformance(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
