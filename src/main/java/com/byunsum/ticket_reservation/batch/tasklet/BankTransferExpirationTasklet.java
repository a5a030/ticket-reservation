package com.byunsum.ticket_reservation.batch.tasklet;

import com.byunsum.ticket_reservation.global.monitoring.SlackNotifier;
import com.byunsum.ticket_reservation.payment.domain.Payment;
import com.byunsum.ticket_reservation.payment.domain.PaymentCancelReason;
import com.byunsum.ticket_reservation.payment.repository.PaymentRepository;
import com.byunsum.ticket_reservation.reservation.domain.Reservation;
import com.byunsum.ticket_reservation.seat.domain.Seat;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class BankTransferExpirationTasklet implements Tasklet {
    private final PaymentRepository paymentRepository;
    private final SlackNotifier slackNotifier;

    public BankTransferExpirationTasklet(PaymentRepository paymentRepository, SlackNotifier slackNotifier) {
        this.paymentRepository = paymentRepository;
        this.slackNotifier = slackNotifier;
    }

    @Override
    @Transactional
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        LocalDate today = LocalDate.now();
        LocalDateTime deadline = today.atTime(23, 59, 59);

        List<Payment> expiredPayments = paymentRepository.findPendingBankTransfersBefore(deadline);

        int count = 0;

        for(Payment expiredPayment : expiredPayments) {
            Reservation reservation = expiredPayment.getReservation();
            reservation.cancel();

            reservation.getSeats().forEach(Seat::release);

            expiredPayment.cancel(PaymentCancelReason.BANK_TRANSFER_EXPIRED);

            count++;
        }

        slackNotifier.send("[BankTransferExpiration] 오늘 무통장 만료 처리 건수: " + count);
        return RepeatStatus.FINISHED;
    }
}
