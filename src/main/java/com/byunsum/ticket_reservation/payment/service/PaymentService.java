package com.byunsum.ticket_reservation.payment.service;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.global.monitoring.SlackNotifier;
import com.byunsum.ticket_reservation.notification.service.NotificationService;
import com.byunsum.ticket_reservation.payment.domain.Payment;
import com.byunsum.ticket_reservation.payment.domain.PaymentStatus;
import com.byunsum.ticket_reservation.payment.dto.PaymentRequest;
import com.byunsum.ticket_reservation.payment.dto.PaymentResponse;
import com.byunsum.ticket_reservation.payment.dto.PaymentStatistics;
import com.byunsum.ticket_reservation.payment.repository.PaymentRepository;
import com.byunsum.ticket_reservation.reservation.domain.Reservation;
import com.byunsum.ticket_reservation.reservation.repository.ReservationRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final StringRedisTemplate redisTemplate;
    private final SlackNotifier slackNotifier;
    private final NotificationService notificationService;

    public PaymentService(PaymentRepository paymentRepository, ReservationRepository reservationRepository, StringRedisTemplate redisTemplate, SlackNotifier slackNotifier, NotificationService notificationService) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
        this.redisTemplate = redisTemplate;
        this.slackNotifier = slackNotifier;
        this.notificationService = notificationService;
    }

    private PaymentResponse toPaymentResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getCreatedAt(),
                payment.getCancelledAt(),
                payment.getAccountNumber()
        );
    }

    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        try {
            Reservation reservation = reservationRepository.findById(request.getReservationId())
                    .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

            reservation.confirm();

            int seatPrice = reservation.getSeat().getPrice();
            int fee = 3000; //예매 수수료
            int totalAmount = seatPrice + fee;

            Payment payment = new Payment(
                    totalAmount,
                    request.getPaymentMethod(),
                    PaymentStatus.PAID,
                    reservation
            );

            if (request.getPaymentMethod().name().equals("BANK_TRANSFER")) {
                String accountNumber = generateAccountNumber();
                payment.setAccountNumber(accountNumber);

                String bankKey = "payment:bank:timeout:" + reservation.getId();

                long secondUntilMidnight = java.time.Duration.between(
                        LocalDateTime.now(),
                        LocalDateTime.now().toLocalDate().plusDays(1).atStartOfDay()
                ).getSeconds();

                redisTemplate.opsForValue().set(bankKey, "PENDING", java.time.Duration.ofSeconds(secondUntilMidnight));

                notificationService.send(
                        reservation.getMember(),
                        "무통장입금 계좌번호: " + accountNumber,
                        "/payments/" + payment.getId()
                );
            }

            Payment saved = paymentRepository.save(payment);

            return new PaymentResponse(
                    saved.getId(),
                    saved.getAmount(),
                    saved.getPaymentMethod(),
                    saved.getStatus(),
                    saved.getCreatedAt(),
                    saved.getCancelledAt(),
                    saved.getAccountNumber()
            );
        } catch (CustomException e) {
            slackNotifier.send("⚠️ 결제 실패: " + e.getErrorCode().name() + " / 요청: " + request);

            throw e;
        } catch (Exception e) {
            slackNotifier.send("🚨 시스템 오류: " + e.getMessage() + " / 요청: " + request);

            throw e;
        }
    }

    @Transactional
    public PaymentResponse cancelPayment(Long id, Long memberId) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

        if(!payment.getReservation().getMember().getId().equals(memberId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_CANCEL);
        }

        if(payment.getStatus() != PaymentStatus.PAID) {
            throw new CustomException(ErrorCode.ALREADY_CANCELED_PAYMENT);
        }

        payment.markAsCancelled();
        payment.getReservation().cancel();

        return toPaymentResponse(payment);
    }

    @Transactional(readOnly = true)
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

    public Long getTotalAmount() {
        return paymentRepository.getTotalPaymentAmount();
    }

    public List<PaymentStatistics> getStatisticsByMethod() {
        return paymentRepository.getPaymentStatistics();
    }

    @Transactional
    public void cancelByReservation(Reservation reservation) {
        Optional<Payment> optional = paymentRepository.findByReservationId(reservation.getId());
        if(optional.isEmpty()) return;

        Payment payment = optional.get();
        if(payment.getStatus() == PaymentStatus.PAID) {
            payment.markAsCancelled();
        }
    }

    private String generateAccountNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for(int i=0; i<10; i++) {
            sb.append(random.nextInt(10));
        }

        return sb.toString();
    }
}
