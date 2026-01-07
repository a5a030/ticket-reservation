package com.byunsum.ticket_reservation.seat.service;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.performance.domain.PerformanceRound;
import com.byunsum.ticket_reservation.performance.repository.PerformanceRoundRepository;
import com.byunsum.ticket_reservation.seat.domain.Seat;
import com.byunsum.ticket_reservation.seat.dto.SeatRequest;
import com.byunsum.ticket_reservation.seat.repository.SeatRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SeatService {
    private final SeatRepository seatRepository;
    private final PerformanceRoundRepository performanceRoundRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public SeatService(SeatRepository seatRepository, PerformanceRoundRepository performanceRoundRepository, RedisTemplate<String, String> redisTemplate) {
        this.seatRepository = seatRepository;
        this.performanceRoundRepository = performanceRoundRepository;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public void createSeat(SeatRequest request) {
        PerformanceRound round = performanceRoundRepository.findById(request.getPerformanceRoundId())
                .orElseThrow(() -> new CustomException(ErrorCode.ROUND_NOT_FOUND));
        round.addSeat(request.getSeatNo(), request.getPrice());
        performanceRoundRepository.save(round);
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
            throw new CustomException(ErrorCode.SEAT_ALREADY_SELECTED);
        }

        seat.setReserved(true);
        redisTemplate.delete(key);
    }
}

