package com.byunsum.ticket_reservation.global.config;

import com.byunsum.ticket_reservation.global.monitoring.SlackNotifier;
import com.byunsum.ticket_reservation.reservation.service.ReservationTtlService;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    private final ReservationTtlService reservationTtlService;
    private final SlackNotifier slackNotifier;

    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer, ReservationTtlService reservationTtlService, SlackNotifier slackNotifier) {
        super(listenerContainer);
        this.reservationTtlService = reservationTtlService;
        this.slackNotifier = slackNotifier;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expireKey = new String(message.getBody(), StandardCharsets.UTF_8);

        try {
            if (expireKey.startsWith("reservation:timeout:")) {
                Long seatId = parseLong(expireKey, "reservation:timeout:");
                reservationTtlService.expireHoldBySeatId(seatId);
                return;
            }

            if (expireKey.startsWith("payment:bank:timeout:")) {
                Long reservationId = parseLong(expireKey, "payment:bank:timeout:");
                reservationTtlService.expireBankTransfer(reservationId);
                return;
            }

            if (expireKey.startsWith("seat:reconfirm:")) {
                Long seatId = parseLong(expireKey, "seat:reconfirm:");
                reservationTtlService.expireReconfirmBySeatId(seatId);
            }
        } catch (Exception e) {
            // TTL 리스너는 예외 던지면 로그만 남고 조용히 누락될 수 있어서 방어적으로 처리
            slackNotifier.send("[TTL] KeyExpiration 처리 실패: key=" + expireKey + ", err=" + e.getMessage());
        }
    }

    private Long parseLong(String key, String prefix) {
        String raw = key.substring(prefix.length()).trim();
        return Long.parseLong(raw);
    }
}
