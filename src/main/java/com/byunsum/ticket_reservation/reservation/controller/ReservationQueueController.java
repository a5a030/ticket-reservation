package com.byunsum.ticket_reservation.reservation.controller;

import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.reservation.service.ReservationQueueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/queue")
@Tag(name = "예매 대기열 API")
public class ReservationQueueController {
    private ReservationQueueService reservationQueueService;

    public ReservationQueueController(ReservationQueueService reservationQueueService) {
        this.reservationQueueService = reservationQueueService;
    }

    @PostMapping("/{performanceId}/join")
    @Operation(summary = "대기열 등록", description = "해당 공연의 예매 대기열에 등록합니다.")
    public String joinQueue(@PathVariable Long performanceId, @AuthenticationPrincipal Member member) {
        return reservationQueueService.joinQueue(performanceId, member.getId());
    }

    @GetMapping("/{performanceId}/position")
    @Operation(summary = "대기열 순번 확인", description = "대기열 내에서 본인의 현재 순번을 확인합니다.")
    public Long getPosition(@PathVariable Long performanceId, @RequestParam String sessionId) {
        return reservationQueueService.getPosition(performanceId, sessionId);
    }

    @GetMapping("/active")
    @Operation(summary = "대기열 활성화 여부 확인", description = "입장 허용된 세션인지 확인합니다.")
    public boolean isActive(@RequestParam String sessionId) {
        return reservationQueueService.isActive(sessionId);
    }

    @PostMapping("/{performanceId}/allow")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "대기열 입장 허용", description = "앞에서 N명의 대기열을 활성화시켜 예매 가능 상태로 만듭니다.")
    public List<String> allowEntry(@PathVariable Long performanceId, @RequestParam int batchSize) {
        return reservationQueueService.allowEntry(performanceId, batchSize);
    }
}
