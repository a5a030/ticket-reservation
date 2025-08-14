package com.byunsum.ticket_reservation.global.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RedisTtlSeatLockTest {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    void seatLockWithTtl_ShouldExpiredAfterGivenTime() throws InterruptedException {
        String key = "seat:101";
        String value = "reserved";

        ValueOperations<String, String> ops = redisTemplate.opsForValue();

        //1. 좌석잠금(TTL 3초)
        ops.set(key, value, 3, TimeUnit.SECONDS);

        //2. 즉시조회
        String storedValue = ops.get(key);
        assertThat(storedValue).isEqualTo(value);

        //3. TTL 만료 전 2초 대기
        Thread.sleep(2000);
        assertThat(ops.get(key)).isEqualTo(value);

        //4. TTL 만료 후 2초 대기
        Thread.sleep(2000);
        String expiredValue = ops.get(key);

        // 5. TTL 만료 후 값이 없어야 함
        assertThat(expiredValue).isNull();
    }
}
