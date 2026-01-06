package com.byunsum.ticket_reservation.global.config;

import com.byunsum.ticket_reservation.global.monitoring.SlackNotifier;
import com.byunsum.ticket_reservation.payment.domain.PaymentCancelReason;
import com.byunsum.ticket_reservation.payment.repository.PaymentRepository;
import com.byunsum.ticket_reservation.reservation.domain.Reservation;
import com.byunsum.ticket_reservation.reservation.domain.ReservationStatus;
import com.byunsum.ticket_reservation.reservation.repository.ReservationRepository;
import com.byunsum.ticket_reservation.reservation.repository.ReservationSeatRepository;
import com.byunsum.ticket_reservation.seat.repository.SeatRepository;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;
    private final SeatRepository seatRepository;
    private final PaymentRepository paymentRepository;
    private final SlackNotifier slackNotifier;

    public RedisKeyExpirationListener(
            RedisMessageListenerContainer listenerContainer,
            ReservationRepository reservationRepository,
            ReservationSeatRepository reservationSeatRepository,
            SeatRepository seatRepository,
            PaymentRepository paymentRepository,
            SlackNotifier slackNotifier
    ) {
        super(listenerContainer);
        this.reservationRepository = reservationRepository;
        this.reservationSeatRepository = reservationSeatRepository;
        this.seatRepository = seatRepository;
        this.paymentRepository = paymentRepository;
        this.slackNotifier = slackNotifier;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expireKey = new String(message.getBody(), StandardCharsets.UTF_8);

        try {
            if (expireKey.startsWith("reservation:timeout:")) {
                Long reservationId = parseLong(expireKey, "reservation:timeout:");
                cancelReservationWithSeats(reservationId, "[TTL] 결제 대기 시간 초과 → 예매 자동 취소");
                return;
            }

            if (expireKey.startsWith("payment:bank:timeout:")) {
                Long reservationId = parseLong(expireKey, "payment:bank:timeout:");
                cancelBankTransferReservation(reservationId);
                return;
            }

            if (expireKey.startsWith("seat:reconfirm:")) {
                Long seatId = parseLong(expireKey, "seat:reconfirm:");
                expireReconfirm(seatId);
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

    @Transactional
    protected void cancelReservationWithSeats(Long reservationId, String reason) {
        reservationRepository.findById(reservationId).ifPresent(reservation -> {
            if (reservation.isCancelled()) return;

            reservation.cancel(); // status=CANCELLED

            reservation.getReservationSeats().forEach(rs -> {
                rs.getSeat().release();
                seatRepository.save(rs.getSeat());
            });

            reservationRepository.save(reservation);
            slackNotifier.send(reason + " (reservationId: " + reservationId + ")");
        });
    }

    @Transactional
    protected void cancelBankTransferReservation(Long reservationId) {
        reservationRepository.findById(reservationId).ifPresent(reservation -> {
            if (reservation.isCancelled()) return;

            reservation.cancel();

            reservation.getReservationSeats().forEach(rs -> {
                rs.getSeat().release();
                seatRepository.save(rs.getSeat());
            });

            reservationRepository.save(reservation);

            paymentRepository.findByReservationId(reservationId).ifPresent(payment -> {
                payment.cancel(PaymentCancelReason.BANK_TRANSFER_EXPIRED);
                paymentRepository.save(payment);

                slackNotifier.send("[TTL] 무통장 미입금 → 결제/예매 자동 취소 (reservationId: "
                        + reservationId + ", paymentId: " + payment.getId() + ")");
            });
        });
    }

    @Transactional
    protected void expireReconfirm(Long seatId) {
        reservationSeatRepository.findBySeatId(seatId).ifPresent(rs -> {
            rs.getSeat().release();
            seatRepository.save(rs.getSeat());

            Reservation reservation = rs.getReservation();
            if (reservation != null && reservation.getStatus() == ReservationStatus.CANCELLED) {
                reservation.setStatus(ReservationStatus.EXPIRED);
                reservationRepository.save(reservation);
            }

            slackNotifier.send("[TTL] 재확정 만료 → 좌석 해제 및 예매 만료 처리 (seatId: " + seatId + ")");
        });
    }
}
