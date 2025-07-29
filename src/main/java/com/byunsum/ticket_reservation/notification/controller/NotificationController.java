package com.byunsum.ticket_reservation.notification.controller;

import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.notification.dto.NotificationResponseDto;
import com.byunsum.ticket_reservation.notification.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponseDto>> getAllNotifications(@AuthenticationPrincipal Member member) {
        return ResponseEntity.ok(notificationService.getAllNotifications(member));
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponseDto>> getUnreadNotifications(@AuthenticationPrincipal Member member) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(member));
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId, @AuthenticationPrincipal Member member) {
        notificationService.markAsRead(notificationId, member);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/test")
    public ResponseEntity<String> testNotification(@AuthenticationPrincipal Member member) {
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        String message = "테스트 알림입니다!";
        notificationService.createNotification(message, member);

        return ResponseEntity.ok("알림 전송 성공!");
    }
}
