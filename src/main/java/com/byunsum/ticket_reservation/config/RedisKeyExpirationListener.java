package com.byunsum.ticket_reservation.config;

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

    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer, ReservationRepository reservationRepository, SeatRepository seatRepository) {
        super(listenerContainer);
        this.reservationRepository = reservationRepository;
        this.seatRepository = seatRepository;
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
                }
            });
        }
    }
}
