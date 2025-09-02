package com.byunsum.ticket_reservation.admin.controller;

import com.byunsum.ticket_reservation.reservation.repository.ReservationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/queue")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "관리자 대기열 통계 API")
public class AdminQueueController {
    private final StringRedisTemplate stringRedisTemplate;
    private final ReservationRepository reservationRepository;

    public AdminQueueController(StringRedisTemplate stringRedisTemplate, ReservationRepository reservationRepository) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.reservationRepository = reservationRepository;
    }

    @GetMapping("/{performanceId}/stats")
    @Operation(summary = "대기열 기본 통계", description = "최대 대기 인원, 기록 시각, 첫 1분 진입자 수, 중복 시도자 수 조회")
    public Map<String, Object> getQueueStats(@PathVariable Long performanceId) {
        String max = stringRedisTemplate.opsForValue().get("waiting:queue:max:" + performanceId);
        String maxTime = stringRedisTemplate.opsForValue().get("waiting:queue:max:time:" + performanceId);
        String firstMinute = stringRedisTemplate.opsForValue().get("waiting:firstMinuteCount:" + performanceId);
        String multiAttempts = stringRedisTemplate.opsForValue().get("waiting:multiAttemptsCount:" + performanceId);

        return Map.of(
                "performanceId", performanceId,
                "maxQueueSize", max != null ? Long.parseLong(max) : 0,
                "maxQueueTime", maxTime != null ? maxTime : "-",
                "firstMinuteJoins", firstMinute != null ? Long.parseLong(firstMinute) : 0,
                "multiAttemptUsers", multiAttempts != null ? Long.parseLong(multiAttempts) : 0
        );
    }

    @GetMapping("/{performanceId}/multi-round-buyers")
    @Operation(summary = "여러 회차 예매자 수", description = "대기열을 통과해 여러 날짜/회차 티켓을 구매한 회원 수를 조회합니다.")
    public Map<String, Object> getMultiRoundBuyers(@PathVariable Long performanceId) {
        List<Object[]> result = reservationRepository.findMembersWithMultipleRounds(performanceId);
        long count = result.size();

        return Map.of(
                "performanceId", performanceId,
                "multiRoundBuyerCount", count
        );
    }
}

