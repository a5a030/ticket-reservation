package com.byunsum.ticket_reservation.payment.controller;

import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.payment.dto.PaymentRequest;
import com.byunsum.ticket_reservation.payment.dto.PaymentResponse;
import com.byunsum.ticket_reservation.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "결제 API", description = "결제 및 결제 취소, 조회 관련 API")
@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(summary = "결제 요청", description = "예매 ID와 결제 정보를 기반으로 결제를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "결제 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping
    public ResponseEntity<PaymentResponse> makePayment(@RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.processPayment(request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "결제 단건 조회", description = "결제 ID를 통해 결제 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable Long id) {
        PaymentResponse response = paymentService.getPayment(id);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "내 결제 목록 조회", description = "로그인한 사용자의 전체 결제 목록을 최신순으로 반환합니다.")
    @GetMapping("/my")
    public ResponseEntity<List<PaymentResponse>> getMyPayments(@AuthenticationPrincipal Member member) {
        List<PaymentResponse> responses = paymentService.getPaymentsByMember(member.getId());

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "결제 취소", description = "결제 ID를 기반으로 결제 상태를 취소로 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "결제 취소 성공"),
            @ApiResponse(responseCode = "400", description = "이미 취소된 결제 혹은 존재하지 않는 결제"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<PaymentResponse> cancelPayment(@PathVariable Long id, @AuthenticationPrincipal Member member) {
        PaymentResponse response = paymentService.cancelPayment(id, member.getId());

        return ResponseEntity.ok(response);
    }
}
