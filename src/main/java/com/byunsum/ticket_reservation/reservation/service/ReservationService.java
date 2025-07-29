package com.byunsum.ticket_reservation.reservation.service;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.performance.domain.Performance;
import com.byunsum.ticket_reservation.performance.repository.PerformanceRepository;
import com.byunsum.ticket_reservation.reservation.domain.Reservation;
import com.byunsum.ticket_reservation.reservation.dto.ReservationRequest;
import com.byunsum.ticket_reservation.reservation.dto.ReservationResponse;
import com.byunsum.ticket_reservation.reservation.repository.ReservationRepository;
import com.byunsum.ticket_reservation.seat.domain.Seat;
import com.byunsum.ticket_reservation.seat.repository.SeatRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final PerformanceRepository performanceRepository;
    private final SeatRepository seatRepository;
    private final StringRedisTemplate redisTemplate;

    public ReservationService(ReservationRepository reservationRepository, PerformanceRepository performanceRepository, SeatRepository seatRepository, StringRedisTemplate redisTemplate) {
        this.reservationRepository = reservationRepository;
        this.performanceRepository = performanceRepository;
        this.seatRepository = seatRepository;
        this.redisTemplate = redisTemplate;
    }

    private String getKey(Long seatId) {
        return "seat:selected:" + seatId;
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

    public ReservationResponse getReservationByCode(String code) {
        Reservation reservation = reservationRepository.findByReservationCode(code)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예매번호입니다."));

        Seat seat = reservation.getSeat();

        return new ReservationResponse(
                reservation.getReservationCode(),
                seat.getSeatNo(),
                seat.getPrice(),
                reservation.getCreatedAt()
        );
    }

    @Transactional
    public ReservationResponse confirmReservation(Long performanceId, Long seatId, Long memberId) {
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> new CustomException(ErrorCode.PERFORMANCE_NOT_FOUND));

        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new CustomException(ErrorCode.SEAT_NOT_FOUND));

        String key = getKey(seatId);
        String selectedBy = redisTemplate.opsForValue().get(key);

        if(selectedBy == null || !selectedBy.equals(memberId.toString())) {
            throw new CustomException(ErrorCode.SEAT_ALREADY_SELECTED);
        }

        if(seat.isReserved()) {
            throw new CustomException(ErrorCode.SEAT_ALREADY_RESERVED);
        }

        seat.setReserved(true);

        Reservation reservation = new Reservation(performance, seat);
        reservationRepository.save(reservation);

        redisTemplate.delete(key);

        return new ReservationResponse(
                reservation.getReservationCode(),
                seat.getSeatNo(),
                seat.getPrice(),
                reservation.getCreatedAt()
        );
    }
}
