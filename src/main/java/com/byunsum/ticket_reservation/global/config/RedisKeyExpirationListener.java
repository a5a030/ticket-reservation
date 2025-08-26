package com.byunsum.ticket_reservation.global.config;

import com.byunsum.ticket_reservation.global.monitoring.SlackNotifier;
import com.byunsum.ticket_reservation.payment.repository.PaymentRepository;
import com.byunsum.ticket_reservation.reservation.domain.Reservation;
import com.byunsum.ticket_reservation.reservation.domain.ReservationStatus;
import com.byunsum.ticket_reservation.reservation.repository.ReservationRepository;
import com.byunsum.ticket_reservation.seat.domain.Seat;
import com.byunsum.ticket_reservation.seat.repository.SeatRepository;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {
    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final PaymentRepository paymentRepository;
    private final SlackNotifier slackNotifier;

    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer, ReservationRepository reservationRepository, SeatRepository seatRepository, PaymentRepository paymentRepository, SlackNotifier slackNotifier) {
        super(listenerContainer);
        this.reservationRepository = reservationRepository;
        this.seatRepository = seatRepository;
        this.paymentRepository = paymentRepository;
        this.slackNotifier = slackNotifier;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expireKey = message.toString();

        if(expireKey.startsWith("reservation:timeout:")){
            Long reservationId = Long.parseLong(expireKey.replace("reservation:timeout:",""));

            reservationRepository.findById(reservationId).ifPresent(reservation -> {
                if(!reservation.isCancelled()) {
                    reservation.cancel();
                    Seat seat = reservation.getSeat();
                    seat.release();
                    seatRepository.save(seat);

                    System.out.println("[TTL] 결제 대기 시간 초과로 예매 자동 취소됨: " + reservationId);
                    slackNotifier.send("[TTL] 결제 대기 시간 초과 → 예매 자동 취소 (ID: " + reservationId + ")");
                }
            });
        }

        if(expireKey.startsWith("payment:bank:timeout:")){
            Long reservationId = Long.parseLong(expireKey.replace("payment:bank:timeout:",""));

            reservationRepository.findById(reservationId).ifPresent(reservation -> {
                if(!reservation.isCancelled()) {
                    reservation.cancel();
                    Seat seat = reservation.getSeat();
                    seat.release();
                    seatRepository.save(seat);

                    System.out.println("[TTL] 무통장입금 미입금으로 예매 자동 취소됨: " + reservationId);
                    slackNotifier.send("[TTL] 무통장입금 미입금 → 예매 자동 취소 (ID: " + reservationId + ")");
                }
            });
        }

        if(expireKey.startsWith("seat:reconfirm:")){
            Long seatId = Long.parseLong(expireKey.replace("seat:reconfirm:",""));
            seatRepository.findById(seatId).ifPresent(seat -> {
                seat.release();
                seatRepository.save(seat);

                Reservation reservation = reservationRepository.findBySeatId(seatId).orElse(null);

                if(reservation != null && reservation.getStatus() == ReservationStatus.CANCELLED) {
                    reservation.setStatus(ReservationStatus.EXPIRED);
                    reservationRepository.save(reservation);
                }

                System.out.println("[TTL] 재확정 가능 시간 만료 → 좌석 해제 및 예매 만료 처리 (seatId: " + seatId + ")");
                slackNotifier.send("[TTL] 재확정 만료 → 좌석 해제 및 예매 만료 처리 (seatId: " + seatId + ")");
            });
        }
    }
}
