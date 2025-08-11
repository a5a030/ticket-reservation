package com.byunsum.ticket_reservation.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket resolveBucket(String key, String path) {
        Bandwidth rule;
        if (path.startsWith("/auth/")) {
            rule = Bandwidth.classic(30, Refill.greedy(30, Duration.ofMinutes(1))); // L: 30/min
        } else if (path.startsWith("/payments/")) {
            rule = Bandwidth.classic(40, Refill.greedy(40, Duration.ofMinutes(1))); // L: 40/min
        } else { // /reservations/
            rule = Bandwidth.classic(60, Refill.greedy(60, Duration.ofMinutes(1))); // L: 60/min
        }
        return buckets.computeIfAbsent(key + "|" + rule.toString(),
                k -> Bucket.builder().addLimit(rule).build());
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
        String key = request.getRemoteAddr(); // 필요시 사용자ID/토큰 등으로 변경
        String auth = request.getHeader("Authorization");
        if(auth != null && auth.startsWith("Bearer ")) {
            key = auth.substring(7);
        }

        Bucket bucket = resolveBucket(key, path);

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(429);
            response.setHeader("Retry-After", "10");
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"too_many_requests\",\"message\":\"Rate limit exceeded\"}");
        }
    }
}
