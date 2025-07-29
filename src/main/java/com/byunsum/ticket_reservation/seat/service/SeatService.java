package com.byunsum.ticket_reservation.seat.service;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.performance.domain.Performance;
import com.byunsum.ticket_reservation.performance.repository.PerformanceRepository;
import com.byunsum.ticket_reservation.seat.domain.Seat;
import com.byunsum.ticket_reservation.seat.dto.SeatRequest;
import com.byunsum.ticket_reservation.seat.repository.SeatRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SeatService {
    private final SeatRepository seatRepository;
    private final PerformanceRepository performanceRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public SeatService(SeatRepository seatRepository, PerformanceRepository performanceRepository, RedisTemplate<String, String> redisTemplate) {
        this.seatRepository = seatRepository;
        this.performanceRepository = performanceRepository;
        this.redisTemplate = redisTemplate;
    }

    public void createSeat(SeatRequest request) {
        Optional<Performance> performanceOpt = performanceRepository.findById(request.getPerformanceId());

        if(performanceOpt.isEmpty()) {
            throw new IllegalArgumentException("해당 ID의 공연이 존재하지 않습니다.");
        }

        Performance performance = performanceOpt.get();

        Seat seat = new Seat(
                request.getSeatNo(),
                request.getPrice(),
                false, //기본 예약 상태 아님
                performance
        );

        seatRepository.save(seat);
    }

    @Transactional
    public void confirmReservation(Long seatId, Long memberId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new CustomException(ErrorCode.SEAT_NOT_FOUND));

        if(seat.isReserved()) {
            throw new CustomException(ErrorCode.SEAT_ALREADY_RESERVED);
        }

        String key = "seat:selected:" + seatId;
        String selectedBy = redisTemplate.opsForValue().get(key);

        if(selectedBy == null || !selectedBy.equals(memberId.toString())) {
            throw new CustomException(ErrorCode.SEAT_ALREADY_RESERVED);
        }

        seat.setReserved(true);
        seatRepository.save(seat);
        redisTemplate.delete(key);
    }
}

