package com.byunsum.ticket_reservation.payment.service;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.payment.domain.Payment;
import com.byunsum.ticket_reservation.payment.domain.PaymentStatus;
import com.byunsum.ticket_reservation.payment.dto.PaymentRequest;
import com.byunsum.ticket_reservation.payment.dto.PaymentResponse;
import com.byunsum.ticket_reservation.payment.repository.PaymentRepository;
import com.byunsum.ticket_reservation.reservation.domain.Reservation;
import com.byunsum.ticket_reservation.reservation.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    public PaymentService(PaymentRepository paymentRepository, ReservationRepository reservationRepository) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        reservation.confirm();

        Payment payment = new Payment(
                request.getAmount(),
                request.getPaymentMethod(),
                PaymentStatus.PAID,
                reservation
        );

        Payment saved = paymentRepository.save(payment);

        return new PaymentResponse(
                saved.getId(),
                saved.getAmount(),
                saved.getPaymentMethod(),
                saved.getStatus(),
                saved.getCreatedAt()
        );
    }
}
