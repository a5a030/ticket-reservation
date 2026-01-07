package com.byunsum.ticket_reservation.seat.controller;

import com.byunsum.ticket_reservation.seat.dto.SeatRequest;
import com.byunsum.ticket_reservation.seat.service.SeatSelectionService;
import com.byunsum.ticket_reservation.seat.service.SeatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "좌석 API", description = "공연 좌석 생성 및 선택 관련 API")
@RestController
@RequestMapping("/admin/seats")
public class SeatController {
    private final SeatService seatService;
    private final SeatSelectionService seatSelectionService;

    public SeatController(SeatService seatService, SeatSelectionService seatSelectionService) {
        this.seatService = seatService;
        this.seatSelectionService = seatSelectionService;
    }

    @Operation(summary = "좌석 생성 (관리자)", description = "공연 ID에 좌석 정보를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "좌석 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping
    public ResponseEntity<Void> createSeat(@Valid @RequestBody SeatRequest request) {
        seatService.createSeat(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "좌석 선택", description = "사용자가 좌석을 선택하여 일시적으로 홀딩합니다.")
    @PostMapping("/select")
    public ResponseEntity<Void> selectSeat(@RequestParam Long seatId, @RequestParam Long memberId) {
        seatSelectionService.selectSeat(seatId, memberId);

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "좌석 선택 상태 조회", description = "특정 좌석이 어떤 사용자에 의해 선택되었는지 확인합니다.")
    @GetMapping("/select/status")
    public ResponseEntity<String> getSeatSelectionStatus(@RequestParam Long seatId) {
        String selectedBy = seatSelectionService.getSeatStatus(seatId);

        return ResponseEntity.ok(selectedBy != null ? selectedBy : "AVAILABLE");
    }

    @DeleteMapping("/select")
    public ResponseEntity<Void> cancelSeatSelection(@RequestParam Long seatId, @RequestParam Long memberId) {
        seatSelectionService.cancelSelection(seatId, memberId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "좌석 예매 확정", description = "좌석 선택 이후 실제 예매를 확정합니다.")
    @PostMapping("/reserve")
    public ResponseEntity<Void> confirmReservation(@RequestParam Long seatId, @RequestParam Long memberId) {
        seatService.confirmReservation(seatId, memberId);
        return ResponseEntity.ok().build();
    }

}
