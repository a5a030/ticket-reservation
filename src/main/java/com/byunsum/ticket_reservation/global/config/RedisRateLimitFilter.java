package com.byunsum.ticket_reservation.global.config;

import com.byunsum.ticket_reservation.security.jwt.JwtTokenProvider;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import io.github.bucket4j.distributed.proxy.ProxyManager;
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
import java.util.function.Supplier;

public class RedisRateLimitFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(RedisRateLimitFilter.class);

    private final JwtTokenProvider jwtTokenProvider;
    private final ProxyManager<String> proxyManager;

    public RedisRateLimitFilter(JwtTokenProvider jwtTokenProvider, ProxyManager<String> proxyManager) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.proxyManager = proxyManager;
    }

    private Bandwidth ruleFor(String path) {
        if (path.startsWith("/auth/")) return Bandwidth.classic(30, Refill.greedy(30, Duration.ofMinutes(1)));
        if (path.startsWith("/payments/")) return Bandwidth.classic(40, Refill.greedy(40, Duration.ofMinutes(1)));
        if (path.startsWith("/reservations/")) return Bandwidth.classic(60, Refill.greedy(60, Duration.ofMinutes(1)));
        return Bandwidth.classic(120, Refill.greedy(120, Duration.ofMinutes(1)));
    }

    private String pathGroup(String path) {
        if (path.startsWith("/auth/")) return "auth";
        if (path.startsWith("/payments/")) return "payments";
        if (path.startsWith("/reservations/")) return "reservations";
        return "default";
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        return !(path.startsWith("/auth/") || path.startsWith("/payments/") || path.startsWith("/reservations/"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        Bandwidth rule = ruleFor(path);
        String group = pathGroup(path);

        String key = clientIp(request);
        String auth = request.getHeader("Authorization");

        if (auth != null && auth.startsWith("Bearer ")) {
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

        Supplier<BucketConfiguration> configSupplier = () -> BucketConfiguration.builder()
                .addLimit(rule)
                .addLimit(Bandwidth.classic(200, Refill.greedy(200, Duration.ofMinutes(1)))) // 전체 API 공통 제한
                .build();

        Bucket bucket = proxyManager.builder()
                .build(compositeKey, configSupplier);

        if (bucket.tryConsume(1)) {
            long remaining = bucket.getAvailableTokens();
            response.setHeader("X-RateLimit-Limit", String.valueOf(rule.getCapacity()));
            response.setHeader("X-RateLimit-Remaining", String.valueOf(remaining));
            if (log.isDebugEnabled()) {
                log.debug("RateLimit OK: key={}, path={}, remaining={}", compositeKey, path, remaining);
            }
            filterChain.doFilter(request, response);
        } else {
            // 초 단위로 변환
            long nanos = rule.getRefillPeriodNanos();
            Duration refillPeriod = Duration.ofNanos(nanos);
            long waitForRefillSeconds = refillPeriod.getSeconds();

            log.warn("Rate limit exceeded: key={}, path={}, retryAfter={}s", key, path, waitForRefillSeconds);
            response.setStatus(429);
            response.setHeader("Retry-After", String.valueOf(waitForRefillSeconds > 0 ? waitForRefillSeconds : 1));
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"too_many_requests\",\"message\":\"Rate limit exceeded\"}");
        }
    }

    private String clientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");

        if(xff != null && !xff.isBlank()){
            String ip = xff.split(",")[0].trim();

            if("::1".equals(ip)){
                ip = "127.0.0.1";
            }

            return ip;
        }

        String remoteAddr = request.getRemoteAddr();

        if("::1".equals(remoteAddr)){
            remoteAddr = "127.0.0.1";
        }

        return remoteAddr;
    }
}
