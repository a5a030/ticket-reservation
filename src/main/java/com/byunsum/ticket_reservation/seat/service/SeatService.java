package com.byunsum.ticket_reservation.seat.service;

import com.byunsum.ticket_reservation.performance.domain.Performance;
import com.byunsum.ticket_reservation.performance.repository.PerformanceRepository;
import com.byunsum.ticket_reservation.seat.domain.Seat;
import com.byunsum.ticket_reservation.seat.dto.SeatRequest;
import com.byunsum.ticket_reservation.seat.repository.SeatRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SeatService {
    private final SeatRepository seatRepository;
    private final PerformanceRepository performanceRepository;

    public SeatService(SeatRepository seatRepository, PerformanceRepository performanceRepository) {
        this.seatRepository = seatRepository;
        this.performanceRepository = performanceRepository;
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
}

