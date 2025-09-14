package com.byunsum.ticket_reservation.payment.repository;

import com.byunsum.ticket_reservation.payment.domain.Payment;
import com.byunsum.ticket_reservation.payment.domain.PaymentStatus;
import com.byunsum.ticket_reservation.payment.dto.PaymentSalesStatsResponse;
import com.byunsum.ticket_reservation.payment.dto.PaymentStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
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
    BigDecimal getTotalPaymentAmount();

    @Query("select count(p) from Payment p where p.status = 'PAID'")
    Long getTotalPaymentCount();

    @Query("SELECT p.paymentMethod AS paymentMethod, COUNT(p) AS count, SUM(p.amount) AS total " +
            "FROM Payment p WHERE p.status = 'PAID' GROUP BY p.paymentMethod")
    List<PaymentStatistics> getPaymentStatistics();

    @Query("select p from Payment p " +
            "where p.paymentMethod = 'BANK_TRANSFER' " +
            "and p.status = 'PENDING' " +
            "and p.createdAt <= :deadline")
    List<Payment> findPendingBankTransfersBefore(LocalDateTime deadline);

    @Query("SELECT new com.byunsum.ticket_reservation.payment.dto.PaymentSalesStatsResponse(" +
            "p.reservation.performance.title, SUM(p.amount), COUNT(p)) " +
            "FROM Payment p " +
            "WHERE p.status = 'PAID' " +
            "GROUP BY p.reservation.performance.title " +
            "ORDER BY SUM(p.amount) DESC")
    List<PaymentSalesStatsResponse> getSalesByPerformance();

    @Query("SELECT new com.byunsum.ticket_reservation.payment.dto.PaymentSalesStatsResponse(" +
            "p.reservation.performance.genre, SUM(p.amount), COUNT(p)) " +
            "FROM Payment p " +
            "WHERE p.status = 'PAID' " +
            "GROUP BY p.reservation.performance.genre " +
            "ORDER BY SUM(p.amount) DESC")
    List<PaymentSalesStatsResponse> getSalesByGenre();
}
