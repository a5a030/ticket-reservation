package com.byunsum.ticket_reservation.reservation.batch;

import com.byunsum.ticket_reservation.performance.repository.PerformanceRepository;
import com.byunsum.ticket_reservation.reservation.service.ReservationQueueService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@EnableScheduling
public class QueueBatchConfig {
    private final ReservationQueueService reservationQueueService;
    private final PerformanceRepository performanceRepository;

    public QueueBatchConfig(ReservationQueueService reservationQueueService, PerformanceRepository performanceRepository) {
        this.reservationQueueService = reservationQueueService;
        this.performanceRepository = performanceRepository;
    }

    @Scheduled(fixedRate = 1000 * 10)
    public void processQueue() {
        List<Long> performanceIds = performanceRepository.findAllIds();

        for(Long performanceId : performanceIds) {
            List<String> entered = reservationQueueService.allowEntry(performanceId, 50);

            if(!entered.isEmpty()) {
                System.out.println("공연 " + performanceId + "에서 " + entered.size() + "명 입장 허용");
            }
        }
    }
}
