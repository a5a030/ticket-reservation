package com.byunsum.ticket_reservation.payment.repository;

import com.byunsum.ticket_reservation.payment.domain.Payment;
import com.byunsum.ticket_reservation.payment.domain.PaymentStatus;
import com.byunsum.ticket_reservation.payment.dto.PaymentStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByReservationId(Long reservationId);

    @Query("SELECT p FROM Payment p WHERE p.reservation.member.id = :memberId ORDER BY p.createdAt DESC")
    List<Payment> findRecentByReservationMemberId(Long memberId);

    List<Payment> findByReservationMemberId(Long memberId);

    List<Payment> findByStatus(PaymentStatus status);

    @Query("SELECT p FROM Payment p WHERE p.status = :status ORDER BY p.createdAt DESC")
    List<Payment> findRecentByStatus(PaymentStatus status);

    @Query("SELECT p FROM Payment p ORDER BY p.createdAt DESC")
    List<Payment> findRecentAll();

    //COALESCE 적용: null일 경우 0 반환
    @Query("SELECT coalesce(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'PAID'")
    Long getTotalPaymentAmount();

    @Query("SELECT p.paymentMethod AS paymentMethod, COUNT(p) AS count, SUM(p.amount) AS total " +
            "FROM Payment p WHERE p.status = 'PAID' GROUP BY p.paymentMethod")
    List<PaymentStatistics> getPaymentStatistics();

    @Query("select p from Payment p " +
            "where p.paymentMethod = 'BANK_TRANSFER' " +
            "and p.status = 'PENDING' " +
            "and p.createdAt <= :deadline")
    List<Payment> findPendingBankTransfersBefore(LocalDateTime deadline);
}
