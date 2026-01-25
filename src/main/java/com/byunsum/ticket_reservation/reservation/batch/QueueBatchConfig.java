package com.byunsum.ticket_reservation.reservation.batch;

import com.byunsum.ticket_reservation.performance.repository.PerformanceRepository;
import com.byunsum.ticket_reservation.reservation.service.ReservationQueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class QueueBatchConfig {
    private static final Logger log = LoggerFactory.getLogger(QueueBatchConfig.class);

    private static final int ENTRY_LIMIT = 50;

    private final ReservationQueueService reservationQueueService;
    private final PerformanceRepository performanceRepository;

    private final AtomicBoolean running = new AtomicBoolean(false);

    public QueueBatchConfig(ReservationQueueService reservationQueueService, PerformanceRepository performanceRepository) {
        this.reservationQueueService = reservationQueueService;
        this.performanceRepository = performanceRepository;
    }

    @Scheduled(fixedRateString = "${queue.scheduler.fixed-rate-ms:10000}")
    public void processQueue() {
        if(!running.compareAndSet(false, true)) {
            log.warn("대기열 스케줄러가 이전 실행 중이라 이번 실행을 건너뜀");
            return;
        }

        long start = System.currentTimeMillis();
        int totalEntered = 0;

        try {
            List<Long> performanceIds = performanceRepository.findAllIds();

            for(Long performanceId : performanceIds) {
                try {
                    List<String> entered = reservationQueueService.allowEntry(performanceId, ENTRY_LIMIT);

                    if(!entered.isEmpty()) {
                        totalEntered += entered.size();
                        log.info("공연 {}에서 {}명 입장 허용", performanceId, entered.size());
                    }
                } catch (Exception e) {
                    log.error("공연 {}에서 대기열 처리 중 예외 발생", performanceId, e);
                }
            }
        } catch (Exception e) {
            log.error("대기열 스케줄러 실행 중 예외 발생", e);
        } finally {
            running.set(false);
            log.info("대기열 스케줄러 종료: totalEntered={}, elapsedMs={}", totalEntered, System.currentTimeMillis() - start);
        }
    }
}
