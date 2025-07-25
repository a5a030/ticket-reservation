package com.byunsum.ticket_reservation.reservation.service;

import com.byunsum.ticket_reservation.performance.domain.Performance;
import com.byunsum.ticket_reservation.performance.repository.PerformanceRepository;
import com.byunsum.ticket_reservation.reservation.domain.Reservation;
import com.byunsum.ticket_reservation.reservation.dto.ReservationRequest;
import com.byunsum.ticket_reservation.reservation.dto.ReservationResponse;
import com.byunsum.ticket_reservation.reservation.repository.ReservationRepository;
import com.byunsum.ticket_reservation.seat.domain.Seat;
import com.byunsum.ticket_reservation.seat.repository.SeatRepository;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final PerformanceRepository performanceRepository;
    private final SeatRepository seatRepository;

    public ReservationService(ReservationRepository reservationRepository, PerformanceRepository performanceRepository, SeatRepository seatRepository) {
        this.reservationRepository = reservationRepository;
        this.performanceRepository = performanceRepository;
        this.seatRepository = seatRepository;
    }

    public ReservationResponse createReservation(ReservationRequest request) {
        Performance performance = performanceRepository.findById(request.getPerformanceId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공연입니다."));

        Seat seat = seatRepository.findById(request.getSeatId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 좌석입니다."));

        if(seat.isReserved()) {
            throw new IllegalStateException("이미 예약된 좌석입니다.");
        }

        seat.setReserved(true);

        Reservation reservation = new Reservation(performance, seat);
        reservationRepository.save(reservation);

        return new ReservationResponse(
                reservation.getReservationCode(),
                seat.getSeatNo(),
                seat.getPrice(),
                reservation.getCreatedAt()
        );
    }
}
