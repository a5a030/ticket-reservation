package com.byunsum.ticket_reservation.config;

import com.byunsum.ticket_reservation.security.jwt.JwtTokenProvider;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class RateLimitFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(RateLimitFilter.class);
    private final JwtTokenProvider jwtTokenProvider;

    public RateLimitFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bandwidth ruleFor(String path) {
        if (path.startsWith("/auth/")) return Bandwidth.classic(30, Refill.greedy(30, Duration.ofMinutes(1))); // L: 30/min
        if (path.startsWith("/payments/")) return Bandwidth.classic(40, Refill.greedy(40, Duration.ofMinutes(1))); // L: 40/min
        if (path.startsWith("/reservations/")) return Bandwidth.classic(60, Refill.greedy(60, Duration.ofMinutes(1))); // L: 60/min

        return Bandwidth.classic(120, Refill.greedy(120, Duration.ofMinutes(1)));
    }

    // 키 안정성 확보 위해 path 그룹명 추출
    private String pathGroup(String path) {
        if (path.startsWith("/auth/")) return "auth";
        if (path.startsWith("/payments/")) return "payments";
        if (path.startsWith("/reservations/")) return "reservations";
        return "default";
    }

    private Bucket resolveBucket(String compositeKey, Bandwidth rule) {
        return buckets.computeIfAbsent(compositeKey, k -> Bucket.builder().addLimit(rule).build());
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !(path.startsWith("/auth/") || path.startsWith("/reservations/") || path.startsWith("/payments/"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        Bandwidth rule = ruleFor(path);
        String group = pathGroup(path);

        // 1. 키 : JWT subject 우선, 없거나 무효면 IP 사용
        String key = clientIp(request); //기본값
        String auth = request.getHeader("Authorization");
        if(auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);

            try {
                if (jwtTokenProvider.validateToken(token)) {
                    key = "sub:" + jwtTokenProvider.getName(token);
                }
            } catch (ExpiredJwtException e) {
                log.debug("Expired JWT for rate limiting: {}", e.getMessage());
            } catch (Exception ignored) {}
        }

        String compositeKey = key + "|" + group;
        Bucket bucket = resolveBucket(compositeKey, rule);

        //2. 소비 시도
        if (bucket.tryConsume(1)) {
            long remaining = bucket.getAvailableTokens();
            response.setHeader("X-RateLimit-Limit", String.valueOf(rule.getCapacity()));
            response.setHeader("X-RateLimit-Remaining", String.valueOf(remaining));

            if(log.isDebugEnabled()) {
                log.debug("RateLimit OK: key={}, path={}, remaining={}", compositeKey, path, remaining);
            }

            filterChain.doFilter(request, response);
        } else {
            //3. 초과: 429 + Retry-After + JSON
            long waitForRefill = rule.getRefillPeriodNanos() / 1_000_000_000L;
            log.warn("Rate limit exceeded: key={}, path={}, retryAfter={}s", key, path,waitForRefill);
            response.setStatus(429);
            response.setHeader("Retry-After", String.valueOf(waitForRefill));
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"too_many_requests\",\"message\":\"Rate limit exceeded\"}");
        }
    }

    // 프록시 환경 고려한 IP 추출
    private String clientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");

        if(xff != null && !xff.isBlank()) {
            int comma = xff.indexOf(',');
            return (comma > 0 ? xff.substring(0, comma) : xff).trim();
        }

        return request.getRemoteAddr();
    }
}
