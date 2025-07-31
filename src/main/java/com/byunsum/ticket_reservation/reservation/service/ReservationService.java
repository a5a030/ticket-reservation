package com.byunsum.ticket_reservation.reservation.service;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.member.domain.Member;
import com.byunsum.ticket_reservation.performance.domain.Performance;
import com.byunsum.ticket_reservation.performance.repository.PerformanceRepository;
import com.byunsum.ticket_reservation.reservation.domain.Reservation;
import com.byunsum.ticket_reservation.reservation.dto.ReservationRequest;
import com.byunsum.ticket_reservation.reservation.dto.ReservationResponse;
import com.byunsum.ticket_reservation.reservation.repository.ReservationRepository;
import com.byunsum.ticket_reservation.seat.domain.Seat;
import com.byunsum.ticket_reservation.seat.repository.SeatRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

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

    private Reservation saveReservation(Performance performance, Seat seat, Member member) {
        seat.setReserved(true);
        Reservation reservation = new Reservation(performance, seat, member);
        reservationRepository.save(reservation);

        return reservation;
    }

    private ReservationResponse toResponse(Reservation reservation) {
        Seat seat = reservation.getSeat();

        return new ReservationResponse(
                reservation.getReservationCode(),
                seat.getSeatNo(),
                seat.getPrice(),
                reservation.getCreatedAt()
        );
    }

    public ReservationResponse createReservation(ReservationRequest request, Member member) {
        Performance performance = performanceRepository.findById(request.getPerformanceId())
                .orElseThrow(() -> new CustomException(ErrorCode.PERFORMANCE_NOT_FOUND));

        Seat seat = seatRepository.findById(request.getSeatId())
                .orElseThrow(() -> new CustomException(ErrorCode.SEAT_NOT_FOUND));


        if(seat.isReserved()) {
            throw new CustomException(ErrorCode.SEAT_ALREADY_RESERVED);
        }

        Reservation reservation = saveReservation(performance, seat, member);

        return toResponse(reservation);
    }

    public ReservationResponse getReservationByCode(String code) {
        Reservation reservation = reservationRepository.findByReservationCode(code)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        Seat seat = reservation.getSeat();

        return new ReservationResponse(
                reservation.getReservationCode(),
                seat.getSeatNo(),
                seat.getPrice(),
                reservation.getCreatedAt()
        );
    }

    @Transactional
    public ReservationResponse confirmReservation(Long performanceId, Long seatId, Member member) {
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> new CustomException(ErrorCode.PERFORMANCE_NOT_FOUND));

        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new CustomException(ErrorCode.SEAT_NOT_FOUND));

        String key = getKey(seatId);
        String selectedBy = redisTemplate.opsForValue().get(key);

        if(selectedBy == null || !selectedBy.equals(member.getId().toString())) {
            throw new CustomException(ErrorCode.SEAT_ALREADY_SELECTED);
        }

        if(seat.isReserved()) {
            throw new CustomException(ErrorCode.SEAT_ALREADY_RESERVED);
        }

        Reservation reservation = saveReservation(performance, seat, member);

        redisTemplate.delete(key);

        return toResponse(reservation);
    }

    @Transactional
    public void cancelReservation(String reservationCode, Long memberId) {
        Reservation reservation = reservationRepository.findByReservationCode(reservationCode)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        if(reservation.isCanceled()) {
            throw new CustomException(ErrorCode.ALREADY_CANCELED);
        }

        if(!reservation.getMember().getId().equals(memberId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_CANCEL);
        }

        reservation.markAsCancelled();

        Seat seat = reservation.getSeat();
        seat.release();
//        // Redis 상태 복구(재예매 가능하게)
//        Long seatId = reservation.getSeat().getId();
//        String key = getKey(seatId);
//        redisTemplate.opsForValue().set(key, "available", Duration.ofMinutes(5));
//
//        reservation.getSeat().setReserved(false);
    }
}
