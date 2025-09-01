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
import com.byunsum.ticket_reservation.performance.domain.Performance;
import com.byunsum.ticket_reservation.performance.domain.PerformanceType;
import com.byunsum.ticket_reservation.reservation.domain.DeliveryMethod;
import com.byunsum.ticket_reservation.reservation.domain.Reservation;
import com.byunsum.ticket_reservation.reservation.repository.ReservationRepository;
import com.byunsum.ticket_reservation.seat.domain.Seat;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

            Performance performance = reservation.getPerformance();

            if(reservation.getPerformance().getType() == PerformanceType.SPORTS) {
                LocalDateTime gameStart = performance.getStartDateTime();
                LocalDateTime bookingDeadline = gameStart.plusHours(1);

                if(LocalDateTime.now().isAfter(bookingDeadline)) {
                    throw new CustomException(ErrorCode.BOOKING_CLOSED);
                }

                reservation.setDeliveryFee(0);
                reservation.setDeliveryMethod(DeliveryMethod.PICKUP);
            }

            reservation.confirm();

            int seatTotal = reservation.getSeats().stream()
                    .mapToInt(Seat::getPrice)
                    .sum();
            int bookingFee = 2000 * reservation.getQuantity();
            int deliveryFee = reservation.getDeliveryFee();
            int totalAmount = seatTotal + bookingFee + deliveryFee;

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

            return toPaymentResponse(saved);
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

        Reservation reservation = payment.getReservation();
        Performance performance = reservation.getPerformance();

        if(reservation.getPerformance().getType() != PerformanceType.SPORTS) {
            validateCancelDeadline(reservation.getPerformance().getStartDate());
        }

        int ticketCount = reservation.getQuantity();
        int seatTotal = reservation.getSeats().stream()
                .mapToInt(seat -> seat.getPrice())
                .sum();
        int bookingFee = 2000 * ticketCount;
        int deliveryFee = reservation.getDeliveryFee();
        int cancelFee;

        if(performance.getType() == PerformanceType.SPORTS) {
            LocalDateTime gameStart = performance.getStartDateTime();
            LocalDateTime cancelDeadline = gameStart.minusHours(4);

            if(LocalDateTime.now().isAfter(cancelDeadline)) {
                throw new CustomException(ErrorCode.CANCEL_NOT_ALLOWED);
            }

            cancelFee = (int) (seatTotal * 0.1);
        } else {
            validateCancelDeadline(performance.getStartDate());
            cancelFee = calculateCancelFee(reservation, seatTotal/ticketCount, ticketCount);
        }
        // 예매 당일이면 bookingFee 환불
        boolean isSameDayBooking = reservation.getCreatedAt().toLocalDate().isEqual(LocalDateTime.now().toLocalDate());
        boolean beforeMidnight = LocalDateTime.now().isBefore(
                reservation.getCreatedAt().toLocalDate().atTime(23,59,59)
        );
        int refundableBookingFee = (isSameDayBooking && beforeMidnight) ? bookingFee : 0;

        int refundAmount = seatTotal - cancelFee + refundableBookingFee;

        payment.markAsCancelled(cancelFee, refundAmount);

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

    private int calculateCancelFee(Reservation reservation, int ticketPrice, int ticketCount) {
        long dayBeforeShow = ChronoUnit.DAYS.between(LocalDateTime.now().toLocalDate(), reservation.getPerformance().getStartDate());
        long daySinceBooking = ChronoUnit.DAYS.between(reservation.getCreatedAt().toLocalDate(), LocalDateTime.now().toLocalDate());

        if(dayBeforeShow >= 10) {
            if(daySinceBooking <= 7) return 0;
            int feePerTicket = Math.min(4000, (int) (ticketPrice * 0.1));
            return feePerTicket * ticketCount;
        } else if(dayBeforeShow >= 7) {
            return (int) (ticketPrice * 0.1) * ticketCount;
        } else if(dayBeforeShow >= 3) {
            return (int) (ticketPrice * 0.2) * ticketCount;
        } else if(dayBeforeShow >= 1) {
            return (int) (ticketPrice * 0.3) * ticketCount;
        } else {
            throw new CustomException(ErrorCode.CANCEL_NOT_ALLOWED);
        }
    }

    @Transactional
    public void restorePayment(Long reservationId) {
        Payment payment = paymentRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

        if(payment.getStatus() != PaymentStatus.PAID) {
            throw new CustomException(ErrorCode.INVALID_PAYMENT_STATUS);
        }

        payment.markAsReconfirmed();
    }

    private void validateCancelDeadline(LocalDate performanceDate) {
        LocalDate dayBefore = performanceDate.minusDays(1);
        DayOfWeek dayOfWeek = dayBefore.getDayOfWeek();

        LocalDateTime deadline;
        if(dayOfWeek == DayOfWeek.SATURDAY) {
            deadline = dayBefore.atTime(11,0);
        } else {
            deadline = dayBefore.atTime(17,0);
        }

        if(LocalDateTime.now().isAfter(deadline)) {
            throw new CustomException(ErrorCode.CANCEL_NOT_ALLOWED);
        }
    }
}
