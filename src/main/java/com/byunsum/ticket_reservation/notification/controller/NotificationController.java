package com.byunsum.ticket_reservation.notification.controller;

import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.notification.domain.NotificationType;
import com.byunsum.ticket_reservation.notification.dto.NotificationResponseDto;
import com.byunsum.ticket_reservation.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "알림 API", description = "마이페이지 알림 조회 및 읽음 처리 API")
@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Operation(summary = "전체 알림 조회", description = "로그인한 사용자의 모든 알림을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "알림 목록 조회 성공")
    @GetMapping
    public ResponseEntity<List<NotificationResponseDto>> getAllNotifications(@AuthenticationPrincipal Member member) {
        return ResponseEntity.ok(notificationService.getAllNotifications(member));
    }

    @Operation(summary = "읽지 않은 알림 조회", description = "로그인한 사용자의 미확인 알림만 조회합니다.")
    @ApiResponse(responseCode = "200", description = "미확인 알림 목록 조회 성공")
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponseDto>> getUnreadNotifications(@AuthenticationPrincipal Member member) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(member));
    }

    @Operation(summary = "알림 읽음 처리", description = "알림 ID를 통해 해당 알림을 읽음 처리합니다.")
    @ApiResponse(responseCode = "200", description = "읽음 처리 성공")
    @Transactional
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId, @AuthenticationPrincipal Member member) {
        notificationService.markAsRead(notificationId, member);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "테스트 알림 전송", description = "현재 로그인한 사용자에게 테스트 알림을 전송합니다.")
    @ApiResponse(responseCode = "200", description = "테스트 알림 전송 성공")
    @PostMapping("/test")
    public ResponseEntity<String> testNotification(@AuthenticationPrincipal Member member) {
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        String message = "테스트 알림입니다!";
        notificationService.createNotification(message, member, NotificationType.SYSTEM);

        return ResponseEntity.ok("알림 전송 성공!");
    }
}
