package com.byunsum.ticket_reservation.reservation.controller;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.reservation.dto.pre.PreReservationMyResponse;
import com.byunsum.ticket_reservation.reservation.dto.pre.PreReservationRequest;
import com.byunsum.ticket_reservation.reservation.dto.pre.PreReservationResponse;
import com.byunsum.ticket_reservation.reservation.service.PreReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pre-reservations")
@Tag(name = "응모", description = "공연 정책에 따라 선예매 자격 추첨 또는 좌석 추첨 응모 API")
public class PreReservationController {
    private final PreReservationService preReservationService;

    public PreReservationController(PreReservationService preReservationService) {
        this.preReservationService = preReservationService;
    }

    @PostMapping
    @Operation(summary = "응모 등록", description = "로그인한 사용자가 공연에 응모합니다. 공연 정책에 따라 선예매 자격(PRE_SALE) 또는 좌석 추첨 응모(SEAT_ASSIGNMENT) 중 하나만 진행됩니다.")
    public ResponseEntity<PreReservationResponse> apply(
            @AuthenticationPrincipal Member member,
            @RequestBody @Valid PreReservationRequest request
    ) {
        if(member == null){
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        PreReservationResponse response = preReservationService.apply(member.getId(),  request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(summary = "내 응모 목록 조회", description = "로그인한 사용자의 응모 내역(선예매 자격/좌석 추첨)을 조회합니다.")
    public ResponseEntity<List<PreReservationMyResponse>> getMyPreReservations(@AuthenticationPrincipal Member member) {
        if(member == null){
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        List<PreReservationMyResponse> responses = preReservationService.getMyPreReservations(member.getId());

        return ResponseEntity.ok(responses);
    }
}
