package com.byunsum.ticket_reservation.global.config;

import com.byunsum.ticket_reservation.global.monitoring.SlackNotifier;
import com.byunsum.ticket_reservation.security.jwt.JwtTokenProvider;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.function.Supplier;

public class RedisRateLimitFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(RedisRateLimitFilter.class);

    private static final String SLACK_THROTTLE_PREFIX = "slack:ratelimit:";
    private static final Duration SLACK_THROTTLE_TTL = Duration.ofSeconds(60);

    private final JwtTokenProvider jwtTokenProvider;
    private final ProxyManager<String> proxyManager;
    private final SlackNotifier slackNotifier;
    private final StringRedisTemplate stringRedisTemplate;

    public RedisRateLimitFilter(JwtTokenProvider jwtTokenProvider,
                                ProxyManager<String> proxyManager,
                                SlackNotifier slackNotifier,
                                StringRedisTemplate stringRedisTemplate) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.proxyManager = proxyManager;
        this.slackNotifier = slackNotifier;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    private Bandwidth ruleFor(String path) {
        if (path.startsWith("/auth/")) return Bandwidth.classic(30, Refill.greedy(30, Duration.ofMinutes(1)));
        if (path.startsWith("/payments/")) return Bandwidth.classic(40, Refill.greedy(40, Duration.ofMinutes(1)));
        if (path.startsWith("/reservations/")) return Bandwidth.classic(60, Refill.greedy(60, Duration.ofMinutes(1)));
        if (path.startsWith("/tickets/verify")) return Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1)));
        return Bandwidth.classic(120, Refill.greedy(120, Duration.ofMinutes(1)));
    }

    private String pathGroup(String path) {
        if (path.startsWith("/auth/")) return "auth";
        if (path.startsWith("/payments/")) return "payments";
        if (path.startsWith("/reservations/")) return "reservations";
        if (path.startsWith("/tickets/verify")) return "ticket_verify";
        return "default";
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return !(path.startsWith("/auth/")
                || path.startsWith("/payments/")
                || path.startsWith("/reservations/")
                || path.startsWith("/tickets/verify"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        Bandwidth rule = ruleFor(path);
        String group = pathGroup(path);

        String key = resolveKey(request);
        String compositeKey = key + "|" + group;

        Supplier<BucketConfiguration> configSupplier = () -> BucketConfiguration.builder()
                .addLimit(rule)
                .addLimit(Bandwidth.classic(200, Refill.greedy(200, Duration.ofMinutes(1)))) // 전체 API 공통 제한
                .build();

        Bucket bucket = proxyManager.builder().build(compositeKey, configSupplier);

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            response.setHeader("X-RateLimit-Limit", String.valueOf(rule.getCapacity()));
            response.setHeader("X-RateLimit-Remaining", String.valueOf(probe.getRemainingTokens()));
            if (log.isDebugEnabled()) {
                log.debug("RateLimit OK: key={}, path={}, remaining={}", compositeKey, path, probe.getRemainingTokens());
            }
            filterChain.doFilter(request, response);
            return;
        }

        long retryAfterSeconds = Math.max(1, Duration.ofNanos(probe.getNanosToWaitForRefill()).getSeconds());

        log.warn("Rate limit exceeded: key={}, path={}, retryAfter={}s", compositeKey, path, retryAfterSeconds);

        response.setStatus(429);
        response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"too_many_requests\",\"message\":\"Rate limit exceeded\"}");

        if (canSendSlack(compositeKey)) {
            slackNotifier.send(String.format(
                    "RateLimit 초과\nKey: %s\nEndpoint: %s\nRetry After: %ds",
                    compositeKey, path, retryAfterSeconds
            ));
        }
    }

    private String resolveKey(HttpServletRequest request) {
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
            } catch (Exception ignored) {
            }
        }

        return key;
    }

    private String clientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");

        if (xff != null && !xff.isBlank()) {
            String ip = xff.split(",")[0].trim();
            if ("::1".equals(ip)) ip = "127.0.0.1";
            return ip;
        }

        String remoteAddr = request.getRemoteAddr();
        if ("::1".equals(remoteAddr)) remoteAddr = "127.0.0.1";
        return remoteAddr;
    }

    private boolean canSendSlack(String compositeKey) {
        try {
            String throttleKey = SLACK_THROTTLE_PREFIX + compositeKey;
            Boolean ok = stringRedisTemplate.opsForValue()
                    .setIfAbsent(throttleKey, "1", SLACK_THROTTLE_TTL);
            return Boolean.TRUE.equals(ok);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Slack throttle check failed. skip slack. reason={}", e.getMessage());
            }
            return false;
        }
    }
}
