package com.byunsum.ticket_reservation.batch.tasklet;

import com.byunsum.ticket_reservation.global.monitoring.SlackNotifier;
import com.byunsum.ticket_reservation.payment.domain.Payment;
import com.byunsum.ticket_reservation.payment.domain.PaymentCancelReason;
import com.byunsum.ticket_reservation.payment.domain.PaymentMethod;
import com.byunsum.ticket_reservation.payment.domain.PaymentStatus;
import com.byunsum.ticket_reservation.payment.repository.PaymentRepository;
import com.byunsum.ticket_reservation.reservation.domain.Reservation;
import com.byunsum.ticket_reservation.seat.domain.Seat;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
public class BankTransferExpirationTasklet implements Tasklet {
    private final PaymentRepository paymentRepository;
    private final SlackNotifier slackNotifier;
    private final StringRedisTemplate stringRedisTemplate;

    public BankTransferExpirationTasklet(PaymentRepository paymentRepository, SlackNotifier slackNotifier, StringRedisTemplate stringRedisTemplate) {
        this.paymentRepository = paymentRepository;
        this.slackNotifier = slackNotifier;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    @Transactional
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        LocalDate today = LocalDate.now();
        LocalDateTime deadline = today.atTime(23, 59, 59);

        List<Payment> expiredPayments = paymentRepository.findPendingBankTransfersBefore(
                PaymentMethod.BANK_TRANSFER,
                PaymentStatus.PENDING,
                deadline
        );

        int count = 0;
        Random random = new Random();

        for(Payment expiredPayment : expiredPayments) {
            expiredPayment.cancel(PaymentCancelReason.BANK_TRANSFER_EXPIRED);
            Reservation reservation = expiredPayment.getReservation();

            for(Seat seat : reservation.getSeats()) {
                String key = "seat:expire:" + seat.getId();
                long ttl = 300 + random.nextInt(300);
                stringRedisTemplate.opsForValue().set(key, "LOCKED", Duration.ofSeconds(ttl));
            }

            count++;
        }

        slackNotifier.send("[BankTransferExpiration] 오늘 무통장 만료 처리 건수: " + count);
        return RepeatStatus.FINISHED;
    }
}
