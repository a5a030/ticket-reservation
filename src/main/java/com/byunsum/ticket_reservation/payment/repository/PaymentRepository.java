package com.byunsum.ticket_reservation.payment.repository;

import com.byunsum.ticket_reservation.payment.domain.Payment;
import com.byunsum.ticket_reservation.payment.domain.PaymentMethod;
import com.byunsum.ticket_reservation.payment.domain.PaymentStatus;
import com.byunsum.ticket_reservation.payment.dto.PaymentSalesStatsResponse;
import com.byunsum.ticket_reservation.payment.dto.PaymentStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByReservationId(Long reservationId);

    @Query("SELECT p FROM Payment p WHERE p.reservation.member.id = :memberId ORDER BY p.createdAt DESC")
    List<Payment> findRecentByReservationMemberId(Long memberId);

    List<Payment> findByStatus(PaymentStatus status);

    @Query("SELECT p FROM Payment p WHERE p.status = :status ORDER BY p.createdAt DESC")
    List<Payment> findRecentByStatus(PaymentStatus status);

    @Query("SELECT p FROM Payment p ORDER BY p.createdAt DESC")
    List<Payment> findRecentAll();

    //COALESCE 적용: null일 경우 0 반환
    @Query("SELECT coalesce(SUM(p.amount), 0) FROM Payment p WHERE p.status = :status")
    BigDecimal getTotalPaymentAmount(PaymentStatus status);

    @Query("select count(p) from Payment p where p.status = :status")
    Long getTotalPaymentCount(PaymentStatus status);

    @Query("SELECT p.paymentMethod AS paymentMethod, COUNT(p) AS count, SUM(coalesce(p.amount, 0)) AS total " +
            "FROM Payment p WHERE p.status = :status GROUP BY p.paymentMethod")
    List<PaymentStatistics> getPaymentStatistics(PaymentStatus status);

    @Query("select p from Payment p " +
            "where p.paymentMethod = :method " +
            "and p.status = :status " +
            "and p.createdAt <= :deadline")
    List<Payment> findPendingBankTransfersBefore(PaymentMethod method, PaymentStatus status, LocalDateTime deadline);

    @Query("SELECT new com.byunsum.ticket_reservation.payment.dto.PaymentSalesStatsResponse(" +
            "p.reservation.performance.title, SUM(coalesce(p.amount, 0)), COUNT(p)) " +
            "FROM Payment p " +
            "WHERE p.status = :status " +
            "GROUP BY p.reservation.performance.title " +
            "ORDER BY SUM(coalesce(p.amount, 0)) DESC")
    List<PaymentSalesStatsResponse> getSalesByPerformance(PaymentStatus status);

    @Query("SELECT new com.byunsum.ticket_reservation.payment.dto.PaymentSalesStatsResponse(" +
            "p.reservation.performance.genre, SUM(coalesce(p.amount, 0)), COUNT(p)) " +
            "FROM Payment p " +
            "WHERE p.status = :status " +
            "GROUP BY p.reservation.performance.genre " +
            "ORDER BY SUM(coalesce(p.amount, 0)) DESC")
    List<PaymentSalesStatsResponse> getSalesByGenre(PaymentStatus status);
}
