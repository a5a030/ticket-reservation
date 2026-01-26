package com.byunsum.ticket_reservation.batch.job.reservation.reader;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import com.byunsum.ticket_reservation.reservation.batch.FakeReservationGenerator;
import com.byunsum.ticket_reservation.reservation.dto.reservation.BatchReservationRequest;
import com.byunsum.ticket_reservation.seat.repository.SeatRepository;
import org.springframework.batch.item.ItemReader;

import java.util.*;

public class FakeReservationReader implements ItemReader<BatchReservationRequest> {
    private final Iterator<BatchReservationRequest> iterator;

    public FakeReservationReader(List<BatchReservationRequest> requests) {
        this.iterator = requests.iterator();
    }

    public FakeReservationReader(SeatRepository seatRepository, int userCount, int requestCount) {
        Map<Long, List<Long>> seatPoolByPerformance = new HashMap<>();

        for(SeatRepository.PerformanceSeatIdRow row : seatRepository.findPerformanceIdAndSeatId()) {
            seatPoolByPerformance
                    .computeIfAbsent(row.getPerformanceId(), k -> new ArrayList<>())
                    .add(row.getSeatId());
        }

        if(seatPoolByPerformance.isEmpty()) {
            throw new CustomException(ErrorCode.SEAT_NOT_FOUND);
        }

        FakeReservationGenerator generator = new FakeReservationGenerator(userCount, seatPoolByPerformance);
        List<BatchReservationRequest> requests = generator.generate(requestCount);
        this.iterator = requests.iterator();
    }

    @Override
    public BatchReservationRequest read(){
        return iterator.hasNext() ? iterator.next() : null;
    }
}
