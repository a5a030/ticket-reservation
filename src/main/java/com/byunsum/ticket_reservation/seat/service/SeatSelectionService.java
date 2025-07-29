package com.byunsum.ticket_reservation.seat.service;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.seat.domain.Seat;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class SeatSelectionService {
    private final StringRedisTemplate redisTemplate;

    public SeatSelectionService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void selectSeat(Long seatId, Long memberId) {
        String key = "seat:selected:" + seatId;

        String selectedBy = redisTemplate.opsForValue().get(key);

        if(selectedBy != null && !selectedBy.equals(memberId.toString())) {
            throw new CustomException(ErrorCode.SEAT_ALREADY_SELECTED);
        }

        redisTemplate.opsForValue().set(key, memberId.toString(), Duration.ofMinutes(5));
    }
}
