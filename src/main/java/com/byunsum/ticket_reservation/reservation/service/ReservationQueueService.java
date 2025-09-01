package com.byunsum.ticket_reservation.reservation.service;

import com.byunsum.ticket_reservation.global.error.CustomException;
import com.byunsum.ticket_reservation.global.error.ErrorCode;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ReservationQueueService {
    private final StringRedisTemplate stringRedisTemplate;

    public ReservationQueueService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public String joinQueue(Long performanceId, Long memberId) {
        String sessionId = UUID.randomUUID().toString();
        String key = "waiting:queue:" + performanceId;

        stringRedisTemplate.opsForList().rightPush(key, sessionId);
        stringRedisTemplate.opsForValue().set("waiting:member:" + sessionId, memberId.toString(), Duration.ofHours(2));

        return sessionId;
    }

    public Long getPosition(Long performanceId, String sessionId) {
        String key = "waiting:queue:" + performanceId;
        var queue = stringRedisTemplate.opsForList().range(key, 0, -1);
        if(queue == null ||!queue.contains(sessionId)) {
            throw new CustomException(ErrorCode.QUEUE_NOT_FOUND);
        }

        return (long) (queue.indexOf(sessionId) + 1);
    }

    public boolean isActive(String sessionId) {
        return stringRedisTemplate.hasKey("waiting:active:"+sessionId);
    }

    @Transactional
    public List<String> allowEntry(Long performanceId, int batchSize) {
        String key = "waiting:queue:" + performanceId;
        List<String> activatedSessions = new ArrayList<>();

        for(int i=0; i<batchSize; i++) {
            String sessionId = stringRedisTemplate.opsForList().leftPop(key);
            if(sessionId == null) break;

            String activeKey = "waiting:active:" + sessionId;
            stringRedisTemplate.opsForValue().set(activeKey, "ACTIVE", Duration.ofMinutes(5));
        }

        return activatedSessions;
    }
}
