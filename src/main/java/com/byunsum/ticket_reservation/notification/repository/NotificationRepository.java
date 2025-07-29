package com.byunsum.ticket_reservation.notification.repository;

import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByMemberOrderByCreatedAtDesc(Member member);
    List<Notification> findByMember(Member member);
    List<Notification> findByMemberAndIsReadFalseOrderByCreatedAtDesc(Member member);
}
