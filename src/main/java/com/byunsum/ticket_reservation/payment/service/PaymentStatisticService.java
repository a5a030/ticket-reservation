package com.byunsum.ticket_reservation.payment.service;

import com.byunsum.ticket_reservation.payment.dto.PaymentSalesStatsResponse;
import com.byunsum.ticket_reservation.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentStatisticService {
    private final PaymentRepository paymentRepository;

    public PaymentStatisticService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public List<PaymentSalesStatsResponse> getSalesByPerformance() {
        return paymentRepository.getSalesByPerformance();
    }

    public List<PaymentSalesStatsResponse> getSalesByGenre() {
        return paymentRepository.getSalesByGenre();
    }
}
