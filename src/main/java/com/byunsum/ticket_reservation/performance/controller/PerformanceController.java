package com.byunsum.ticket_reservation.performance.controller;

import com.byunsum.ticket_reservation.performance.dto.PerformanceRequest;
import com.byunsum.ticket_reservation.performance.dto.PerformanceResponse;
import com.byunsum.ticket_reservation.performance.service.PerformanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/performances")
public class PerformanceController {
    private final PerformanceService performanceService;

    public PerformanceController(PerformanceService performanceService) {
        this.performanceService = performanceService;
    }

    @PostMapping
    public ResponseEntity<Void> savePerformance(@RequestBody PerformanceRequest request) {
        performanceService.createPerformance(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/public")
    public ResponseEntity<List<PerformanceResponse>> getAllPerformance() {
        List<PerformanceResponse> performances = performanceService.getAllPerformances();
        return ResponseEntity.ok(performances);
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<PerformanceResponse> getPerformanceById(@PathVariable Long id) {
        PerformanceResponse response = performanceService.getPerformanceById(id);
        return ResponseEntity.ok(response);
    }
}
