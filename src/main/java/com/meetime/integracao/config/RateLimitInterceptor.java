package com.meetime.integracao.config;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final ConcurrentHashMap<String, Bucket> rateLimitCache = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientId = resolveClientId(request);

        Bucket bucket = rateLimitCache.computeIfAbsent(clientId, this::newBucket);

        if (bucket.tryConsume(1)) {
            return true;
        } else {
            response.setStatus(429);
            response.getWriter().write("Você atingiu o limite de requisições. Tente novamente mais tarde.");
            return false;
        }
    }

    private Bucket newBucket(String clientId) {
        Bandwidth limit = Bandwidth.classic(110, Refill.greedy(110, Duration.ofSeconds(10)));
        return Bucket.builder().addLimit(limit).build();
    }

    private String resolveClientId(HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        return clientIp;
    }
}

