package com.byunsum.ticket_reservation.payment.service;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.payment.domain.Payment;
import com.byunsum.ticket_reservation.payment.domain.PaymentMethod;
import com.byunsum.ticket_reservation.payment.domain.PaymentStatus;
import com.byunsum.ticket_reservation.payment.repository.PaymentRepository;
import com.byunsum.ticket_reservation.performance.domain.Performance;
import com.byunsum.ticket_reservation.reservation.domain.Reservation;
import com.byunsum.ticket_reservation.reservation.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.concurrent.CancellationException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

public class PaymentServiceTest {
    private PaymentService paymentService;
    private PaymentRepository paymentRepository;
    private ReservationRepository reservationRepository;

    @BeforeEach
    public void setup() {
        paymentRepository = Mockito.mock(PaymentRepository.class);
        reservationRepository = Mockito.mock(ReservationRepository.class);
        paymentService = new PaymentService(
                paymentRepository,
                reservationRepository,
                null, null, null
        );
    }

    @Test
    void cancelPayment_shouldFail_ReservationIsShipped() {
        Member member = new Member();
        member.setId(1L);

        Performance performance = new Performance();
        Reservation reservation = new Reservation();
        reservation.setMember(member);
        reservation.setPerformance(performance);
        reservation.markAsShipped();

        Payment payment = new Payment(10000, PaymentMethod.CARD, PaymentStatus.PAID, reservation);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.cancelPayment(1L, 1L))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.CANCEL_NOT_ALLOWED.getMessage());
    }
}
