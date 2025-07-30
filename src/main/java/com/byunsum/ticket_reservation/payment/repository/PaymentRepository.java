package com.byunsum.ticket_reservation.payment.repository;

import com.byunsum.ticket_reservation.payment.domain.Payment;
import com.byunsum.ticket_reservation.payment.domain.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByReservationId(Long reservationId);
    List<Payment> findByReservationMemberId(Long memberId);
    List<Payment> findByStatus(PaymentStatus status);
}
