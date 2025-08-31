package com.byunsum.ticket_reservation.reservation.batch;

import com.byunsum.ticket_reservation.reservation.dto.ReservationRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class FakeReservationGenerator {
    private final int userCount;
    private final int performanceCount;
    private final int seatPerPerformance;

    public FakeReservationGenerator(int userCount, int performanceCount, int seatPerPerformance) {
        this.userCount = userCount;
        this.performanceCount = performanceCount;
        this.seatPerPerformance = seatPerPerformance;
    }

    public List<ReservationRequest> generate(int count) {
        List<ReservationRequest> requests = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Long memberId = (long) ThreadLocalRandom.current().nextInt(1, userCount+1);
            Long performanceId = (long) ThreadLocalRandom.current().nextInt(1, performanceCount+1);

            int seatCount = ThreadLocalRandom.current().nextInt(1, 4);
            List<Long> seatIds = new ArrayList<>();

            for(int j=0; j<seatCount; j++){
                Long seatId = (long) ThreadLocalRandom.current().nextInt(1, seatPerPerformance+1);
                seatIds.add(seatId);
            }

            requests.add(new ReservationRequest(memberId, performanceId, seatIds));
        }

        return requests;
    }
}
