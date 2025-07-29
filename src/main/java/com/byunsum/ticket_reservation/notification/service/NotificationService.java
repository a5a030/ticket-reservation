package com.byunsum.ticket_reservation.notification.service;

import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.notification.domain.Notification;
import com.byunsum.ticket_reservation.notification.dto.NotificationResponseDto;
import com.byunsum.ticket_reservation.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void createNotification(String message, Member member) {
        Notification notification = new Notification(message, member);
        notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsForMember(Member member) {
        return notificationRepository.findByMemberOrderByCreatedAtDesc(member);
    }

    public void markAsRead(Long notiId, Member member) {
        Notification notification = notificationRepository.findById(notiId)
                .orElseThrow(() -> new IllegalArgumentException("알림이 존재하지 않습니다."));

        if(!notification.getMember().getId().equals(member.getId())) {
            throw new IllegalStateException("해당 사용자의 알림이 아닙니다.");
        }

        notification.markAsRead(true);
    }

    public List<NotificationResponseDto> getAllNotifications(Member member) {
        List<Notification> notifications = notificationRepository.findByMember(member);

        return notifications.stream()
                .map(NotificationResponseDto::new)
                .collect(Collectors.toList());
    }

    public List<NotificationResponseDto> getUnreadNotifications(Member member) {
        List<Notification> unread = notificationRepository.findByMemberAndIsReadFalseOrderByCreatedAtDesc(member);

        return unread.stream()
                .map(NotificationResponseDto::new)
                .collect(Collectors.toList());
    }
}
