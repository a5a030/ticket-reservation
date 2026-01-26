package com.byunsum.ticket_reservation.reservation.batch;

import com.byunsum.ticket_reservation.reservation.dto.reservation.BatchReservationRequest;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class FakeReservationGenerator {
    private final int userCount;
    private final Map<Long, List<Long>> seatPoolByPerformance;

    public FakeReservationGenerator(int userCount, Map<Long, List<Long>> seatPoolByPerformance) {
        this.userCount = userCount;
        this.seatPoolByPerformance = seatPoolByPerformance;
    }

    public List<BatchReservationRequest> generate(int count) {
        List<BatchReservationRequest> requests = new ArrayList<>();
        if(count<=0) {
            return requests;
        }

        List<Long> performanceIds = new ArrayList<>(seatPoolByPerformance.keySet());
        if(performanceIds.isEmpty()) {
            return requests;
        }

        for (int i = 0; i < count; i++) {
            Long memberId = (long) ThreadLocalRandom.current().nextInt(1, userCount+1);
            Long performanceId = performanceIds.get(ThreadLocalRandom.current().nextInt(performanceIds.size()));

            List<Long> seatPool = seatPoolByPerformance.get(performanceId);
            if(seatPool == null || seatPool.isEmpty()) {
                continue;
            }

            int seatCount = ThreadLocalRandom.current().nextInt(1, 4);
            seatCount = Math.min(seatCount, seatPool.size());

            Set<Long> seatIdsSet = new HashSet<>();

            while (seatIdsSet.size() < seatCount) {
                Long seatId = seatPool.get(ThreadLocalRandom.current().nextInt(seatPool.size()));
                seatIdsSet.add(seatId);
            }

            List<Long> seatIds = new ArrayList<>(seatIdsSet);
            requests.add(new BatchReservationRequest(memberId, performanceId, seatIds));
        }

        return requests;
    }
}
