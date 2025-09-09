package com.byunsum.ticket_reservation.admin.controller;

import com.byunsum.ticket_reservation.payment.dto.PaymentSalesStatsResponse;
import com.byunsum.ticket_reservation.payment.service.PaymentStatisticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/payments/statistics")
@Tag(name = "관리자 결제 통계 API", description = "공연별/장르별 매출 통계 조회")
public class AdminPaymentStatisticsController {
    private final PaymentStatisticService paymentStatisticService;

    public AdminPaymentStatisticsController(PaymentStatisticService paymentStatisticService) {
        this.paymentStatisticService = paymentStatisticService;
    }

    @GetMapping("/performance")
    @Operation(summary = "공연별 매출 통계", description = "공연별 매출액과 결제 건수를 집계합니다.")
    public ResponseEntity<List<PaymentSalesStatsResponse>> getSalesByPerformance() {
        return ResponseEntity.ok(paymentStatisticService.getSalesByPerformance());
    }

    @GetMapping("/genre")
    @Operation(summary = "장르별 매출 통계", description = "장르별 매출액과 결제 건수를 집계합니다.")
    public ResponseEntity<List<PaymentSalesStatsResponse>> getSalesByGenre() {
        return ResponseEntity.ok(paymentStatisticService.getSalesByGenre());
    }
}
