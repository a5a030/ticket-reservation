package com.byunsum.ticket_reservation.reservation.controller;

import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.reservation.dto.pre.PreReservationMyResponse;
import com.byunsum.ticket_reservation.reservation.dto.pre.PreReservationRequest;
import com.byunsum.ticket_reservation.reservation.dto.pre.PreReservationResponse;
import com.byunsum.ticket_reservation.reservation.service.PreReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pre-reservations")
@Tag(name = "선예매 응모", description = "선예매 응모 API")
public class PreReservationController {
    private PreReservationService preReservationService;

    public PreReservationController(PreReservationService preReservationService) {
        this.preReservationService = preReservationService;
    }

    @PostMapping
    @Operation(summary = "선예매 응모 등록", description = "로그인한 사용자가 공연 회차에 선예매 응모를 등록합니다.")
    public ResponseEntity<PreReservationResponse> apply(
            @AuthenticationPrincipal Member member,
            @RequestBody PreReservationRequest request
    ) {
        PreReservationResponse response = preReservationService.apply(member.getId(),  request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(summary = "", description = "")
    public ResponseEntity<List<PreReservationMyResponse>> getMyPreReservations(@AuthenticationPrincipal Member member) {
        List<PreReservationMyResponse> responses = preReservationService.getMyPreReservations(member.getId());

        return ResponseEntity.ok(responses);
    }
}
