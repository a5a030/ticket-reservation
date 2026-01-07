package com.byunsum.ticket_reservation.payment.service;

import com.byunsum.ticket_reservation.payment.domain.PaymentStatus;
import com.byunsum.ticket_reservation.payment.dto.PaymentSalesStatsResponse;
import com.byunsum.ticket_reservation.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PaymentStatisticService {
    private final PaymentRepository paymentRepository;

    public PaymentStatisticService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public List<PaymentSalesStatsResponse> getSalesByPerformance() {
        return paymentRepository.getSalesByPerformance(PaymentStatus.PAID);
    }

    public List<PaymentSalesStatsResponse> getSalesByGenre() {
        return paymentRepository.getSalesByGenre(PaymentStatus.PAID);
    }

    public List<PaymentSalesStatsResponse> getTopPerformances(int limit) {
        return getSalesByPerformance()
                .stream()
                .limit(limit)
                .toList();
    }

    public List<PaymentSalesStatsResponse> getTopGenres(int limit) {
        return getSalesByGenre()
                .stream()
                .limit(limit)
                .toList();
    }

    public BigDecimal getTotalPaymentAmount() {
        return paymentRepository.getTotalPaymentAmount(PaymentStatus.PAID);
    }

    public Long getTotalPaymentCount() {
        return paymentRepository.getTotalPaymentCount(PaymentStatus.PAID);
    }

    public BigDecimal getAveragePaymentAmount() {
        BigDecimal totalAmount = getTotalPaymentAmount();
        Long totalCount = getTotalPaymentCount();

        return totalCount > 0
                ? totalAmount.divide(BigDecimal.valueOf(totalCount), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
    }

}
