package com.byunsum.ticket_reservation.reservation.batch;

import com.byunsum.ticket_reservation.performance.domain.Performance;
import com.byunsum.ticket_reservation.performance.repository.PerformanceRepository;
import com.byunsum.ticket_reservation.reservation.service.ReservationQueueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class QueueBatchIntegrationTest {
    @Autowired
    private ReservationQueueService reservationQueueService;

    @Autowired
    private PerformanceRepository performanceRepository;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @BeforeEach
    public void setup() {
        stringRedisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    void testBatchAllowEntry() {
        //given
        Long performanceId = 999L;

        for(long memberId=1; memberId<=5; memberId++) {
            reservationQueueService.joinQueue(performanceId, memberId);
        }

        Long size = stringRedisTemplate.opsForList().size("waiting:queue:" + performanceId);
        System.out.println("Redis queue size before allowEntry = " + size);

        //when
        List<String> entered = reservationQueueService.allowEntry(performanceId,3);

        System.out.println("Entered = " + entered);

        //then
        assertThat(entered).hasSize(3);
        assertThat(reservationQueueService.isActive(entered.get(0))).isTrue();

        Long remaining = stringRedisTemplate.opsForList().size("waiting:queue:" + performanceId);
        assertThat(remaining).isEqualTo(2L);
    }
}
