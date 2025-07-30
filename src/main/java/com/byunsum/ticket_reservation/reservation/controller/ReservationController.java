package com.byunsum.ticket_reservation.reservation.controller;

import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.reservation.dto.ReservationRequest;
import com.byunsum.ticket_reservation.reservation.dto.ReservationResponse;
import com.byunsum.ticket_reservation.reservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "예매 API", description = "공연 예매 관련 API")
@RestController
@RequestMapping("/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(summary = "예매 생성", description = "예매 요청을 통해 예매 정보를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "예매 생성 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 예매 요청"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @PostMapping
    public ResponseEntity<ReservationResponse> reservation(@RequestBody ReservationRequest request, @AuthenticationPrincipal Member member) {
        ReservationResponse response = reservationService.createReservation(request, member);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "예매 조회", description = "예매 코드로 예매 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "예매 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 예매가 존재하지 않음")
    })
    @GetMapping("/{code}")
    public ResponseEntity<ReservationResponse> getReservation(@PathVariable String code) {
        ReservationResponse response = reservationService.getReservationByCode(code);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "예매 확정", description = "공연 ID와 좌석 ID를 통해 예매를 확정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "예매 확정 성공"),
            @ApiResponse(responseCode = "400", description = "좌석 정보가 잘못되었거나 이미 예매됨")
    })
    @PostMapping("/confirm")
    public ResponseEntity<ReservationResponse> confirmReservation(@RequestParam Long performanceId, @RequestParam Long seatId, @AuthenticationPrincipal Member member) {
        ReservationResponse response = reservationService.confirmReservation(performanceId, seatId, member);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "예매 취소", description = "예매 코드를 통해 예매를 취소합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "예매 취소 성공"),
            @ApiResponse(responseCode = "403", description = "본인의 예매가 아님"),
            @ApiResponse(responseCode = "404", description = "예매 내역 없음")
    })
    @DeleteMapping("/{code}/cancel")
    public ResponseEntity<Void> cancelReservation(@PathVariable String code, @AuthenticationPrincipal Member member) {
        reservationService.cancelReservation(code, member.getId());

        return ResponseEntity.ok().build();
    }
}
