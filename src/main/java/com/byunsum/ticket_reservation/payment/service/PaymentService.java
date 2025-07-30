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

import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    public PaymentService(PaymentRepository paymentRepository, ReservationRepository reservationRepository) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
    }

    private PaymentResponse toPaymentResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getCreatedAt()
        );
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

    @Transactional
    public void cancelPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

        if(payment.getStatus() != PaymentStatus.PAID) {
            throw new CustomException(ErrorCode.ALREADY_CANCELED_PAYMENT);
        }

        payment.markAsCancelled();
        payment.getReservation().cancel();
    }

    public PaymentResponse getPayment(Long id) {
        Payment payment =  paymentRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

        return toPaymentResponse(payment);
    }

    public PaymentResponse getPaymentByReservationId(Long reservationId) {
        Payment payment = paymentRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

        return toPaymentResponse(payment);
    }

    public List<PaymentResponse> getPaymentsByMember(Long memberId) {
        List<Payment> payments = paymentRepository.findByReservationMemberId(memberId);

        return payments.stream()
                .map(this::toPaymentResponse)
                .toList();
    }

    public List<PaymentResponse> getAllPayments(Optional<PaymentStatus> status) {
        List<Payment> payments = status
                .map(paymentRepository::findRecentByStatus)
                .orElseGet(paymentRepository::findAll);

        return  payments.stream()
                .map(this::toPaymentResponse)
                .toList();
    }

    public List<PaymentResponse> getPaymentsUnsorted(Long memberId) {
        List<Payment> payments = paymentRepository.findByReservationMemberId(memberId);
        return payments.stream()
                .map(this::toPaymentResponse)
                .toList();
    }
}
