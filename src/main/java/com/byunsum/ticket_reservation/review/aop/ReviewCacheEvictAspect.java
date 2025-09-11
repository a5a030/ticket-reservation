package com.byunsum.ticket_reservation.review.aop;

import com.byunsum.ticket_reservation.reservation.repository.ReservationRepository;
import com.byunsum.ticket_reservation.review.dto.ReviewRequest;
import com.byunsum.ticket_reservation.review.repository.ReviewRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

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

    @AfterReturning("@annotation(invalidateReviewCache)")
    public void evictReviewCache(JoinPoint joinPoint, InvalidateReviewCache invalidateReviewCache) {
        Long performanceId = resolvePerformanceId(joinPoint.getArgs());

        if (performanceId != null) {
            String cacheKey = "review:dashboard:performanceId:" + performanceId;
            stringRedisTemplate.delete(cacheKey);
            log.info("✅ 리뷰 대시보드 캐시 삭제: {}", cacheKey);
        } else {
            log.warn("⚠️ 캐시 삭제 실패: performanceId 추출 불가 (args={})", (Object) joinPoint.getArgs());
        }

    }

    private Long resolvePerformanceId(Object[] args) {
        for(Object arg : args) {
            if(arg instanceof ReviewRequest request) {
                // 리뷰 작성 시: reservationId → performanceId 추출
                return reservationRepository.findById(request.getReservationId())
                        .map(r -> r.getPerformance().getId())
                        .orElse(null);
            } else if(arg instanceof Long id) {
                // 리뷰 수정/삭제 시: reviewId → performanceId 추출
                return reviewRepository.findById(id)
                        .map(r -> r.getReservation().getPerformance().getId())
                        .orElse(null);
            }
        }

        return null;
    }
}
