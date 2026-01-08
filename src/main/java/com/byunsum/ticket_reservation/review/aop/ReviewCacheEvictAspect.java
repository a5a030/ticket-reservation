package com.byunsum.ticket_reservation.review.aop;

import com.byunsum.ticket_reservation.reservation.repository.ReservationRepository;
import com.byunsum.ticket_reservation.review.dto.ReviewRequest;
import com.byunsum.ticket_reservation.review.repository.ReviewRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class ReviewCacheEvictAspect {
    private static final Logger log = LoggerFactory.getLogger(ReviewCacheEvictAspect.class);

    private final StringRedisTemplate stringRedisTemplate;
    private final ReservationRepository reservationRepository;
    private final ReviewRepository reviewRepository;

    public ReviewCacheEvictAspect(StringRedisTemplate stringRedisTemplate, ReservationRepository reservationRepository, ReviewRepository reviewRepository) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.reservationRepository = reservationRepository;
        this.reviewRepository = reviewRepository;
    }

    @Around("@annotation(invalidateReviewCache)")
    public Object evictReviewCache(ProceedingJoinPoint joinPoint, InvalidateReviewCache invalidateReviewCache) throws Throwable {
        Long performanceId = resolvePerformanceId(joinPoint.getArgs());

        Object result = joinPoint.proceed();

        if (performanceId == null) {
            log.warn("리뷰 캐시 삭제 스킵: performanceId 추출 실패 (args={})",
                    Arrays.toString(joinPoint.getArgs()));

            return result;
        }

        String dashboardKey = ReviewCacheKeys.dashboardKey(performanceId);
        String keywordsKey = ReviewCacheKeys.keywordsKey(performanceId);

        try {
            Boolean d1 = stringRedisTemplate.delete(dashboardKey);
            Boolean d2 = stringRedisTemplate.delete(keywordsKey);

            log.info("리뷰 캐시 삭제 완료 performanceId={} (dashboardKey={}, deleted={}, keywordsKey={}, deleted={})", performanceId, dashboardKey, d1, keywordsKey, d2);
        } catch (Exception e) {
            log.warn("리뷰 캐시 삭제 실패 performanceId={} (message={})", performanceId, e.getMessage());
        }

        return result;
    }

    private Long resolvePerformanceId(Object[] args) {
        for(Object arg : args) {
            if(arg instanceof ReviewRequest request) {
                Long reservationId = request.getReservationId();

                if(reservationId == null) return null;

                // 리뷰 작성 시: reservationId → performanceId 추출
                return reservationRepository.findById(reservationId)
                        .map(r -> r.getPerformance().getId())
                        .orElse(null);
            }

            if(arg instanceof Long id) {
                // 리뷰 수정/삭제 시: reviewId → performanceId 추출
                return reviewRepository.findById(id)
                        .map(r -> r.getReservation().getPerformance().getId())
                        .orElse(null);
            }
        }

        return null;
    }
}
