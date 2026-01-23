package com.byunsum.ticket_reservation.reservation.service;

import com.byunsum.ticket_reservation.global.monitoring.SlackNotifier;
import com.byunsum.ticket_reservation.payment.domain.Payment;
import com.byunsum.ticket_reservation.payment.domain.PaymentCancelReason;
import com.byunsum.ticket_reservation.payment.domain.PaymentStatus;
import com.byunsum.ticket_reservation.payment.repository.PaymentRepository;
import com.byunsum.ticket_reservation.reservation.domain.pre.Reservation;
import com.byunsum.ticket_reservation.reservation.domain.reservation.ReservationSeat;
import com.byunsum.ticket_reservation.reservation.domain.reservation.ReservationSeatStatus;
import com.byunsum.ticket_reservation.reservation.domain.reservation.ReservationStatus;
import com.byunsum.ticket_reservation.reservation.repository.ReservationRepository;
import com.byunsum.ticket_reservation.reservation.repository.ReservationSeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ReservationTtlService {
    private final ReservationSeatRepository reservationSeatRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final SlackNotifier slackNotifier;

    public ReservationTtlService(ReservationSeatRepository reservationSeatRepository, ReservationRepository reservationRepository, PaymentRepository paymentRepository, SlackNotifier slackNotifier) {
        this.reservationSeatRepository = reservationSeatRepository;
        this.reservationRepository = reservationRepository;
        this.paymentRepository = paymentRepository;
        this.slackNotifier = slackNotifier;
    }

    @Transactional
    public void expireHoldBySeatId(Long seatId) {
        ReservationSeat rs = reservationSeatRepository.findBySeatId(seatId)
                .orElse(null);
        if (rs == null) return;

        if(rs.getStatus() != ReservationSeatStatus.HOLD) return;

        LocalDateTime now = LocalDateTime.now();
        rs.systemRelease(now);

        try {
            slackNotifier.send("[TTL] HOLD 만료 → 좌석 해제 (seatId: "+ seatId + ")");
        } catch (Exception ignored) {}
    }

    @Transactional
    public void expireReconfirmBySeatId(Long seatId) {
        ReservationSeat rs = reservationSeatRepository.findBySeatId(seatId).orElse(null);
        if (rs == null) return;

        Reservation reservation = rs.getReservation();
        if(reservation == null) return;

        if(reservation.getStatus() != ReservationStatus.CANCELLED) return;

        LocalDateTime now = LocalDateTime.now();

        reservation.getReservationSeats().forEach(seat -> {
            if(seat.getStatus() == ReservationSeatStatus.CANCELLED) {
                seat.systemRelease(now);
            }
        });

        try {
            slackNotifier.send("[TTL] 재확정 만료 → 좌석 해제 (seatId: " + seatId + ", reservationId: " + reservation.getId() + ")");
        } catch (Exception ignored) {}
    }

    @Transactional
    public void expireBankTransfer(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElse(null);
        if (reservation == null) return;

        if (reservation.getStatus() == ReservationStatus.CANCELLED) return;
        if(reservation.getStatus() == ReservationStatus.EXPIRED) return;

        Payment payment = paymentRepository.findByReservationId(reservationId).orElse(null);
        if (payment == null) return;
        if (payment.getStatus() != PaymentStatus.PENDING) return;

        LocalDateTime now = LocalDateTime.now();

        reservation.expireAll(now);

        for (ReservationSeat seat : reservation.getReservationSeats()) {
            if (seat.getStatus() == ReservationSeatStatus.CANCELLED) {
                seat.systemRelease(now);
            }
        }

        payment.cancel(PaymentCancelReason.BANK_TRANSFER_EXPIRED);
        paymentRepository.save(payment);

        try {
            slackNotifier.send("[TTL] 무통장 미입금 → 결제/예매 자동 만료 (reservationId: "
                    + reservationId + ", paymentId: " + payment.getId() + ")");
        } catch (Exception ignored) {}
    }
}
