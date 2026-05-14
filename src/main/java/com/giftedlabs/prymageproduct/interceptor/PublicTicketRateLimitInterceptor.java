package com.giftedlabs.prymageproduct.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class PublicTicketRateLimitInterceptor implements HandlerInterceptor {

    private final int maxRequests;
    private final Duration window = Duration.ofHours(1);
    private final Map<String, Deque<Instant>> requests = new ConcurrentHashMap<>();

    public PublicTicketRateLimitInterceptor(@Value("${app.rate-limit.public-tickets:5}") int maxRequests) {
        this.maxRequests = maxRequests;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ip = request.getRemoteAddr();
        Instant now = Instant.now();
        Instant threshold = now.minus(window);

        Deque<Instant> ipRequests = requests.computeIfAbsent(ip, key -> new ArrayDeque<>());
        synchronized (ipRequests) {
            while (!ipRequests.isEmpty() && ipRequests.peekFirst().isBefore(threshold)) {
                ipRequests.pollFirst();
            }
            if (ipRequests.size() >= maxRequests) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"type\":\"/errors/rate-limit\",\"title\":\"Too Many Requests\",\"status\":429,\"detail\":\"Rate limit exceeded for public ticket submission\"}");
                return false;
            }
            ipRequests.addLast(now);
        }
        return true;
    }
}
