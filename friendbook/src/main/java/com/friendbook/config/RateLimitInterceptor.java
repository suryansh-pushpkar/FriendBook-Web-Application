package com.friendbook.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final long WINDOW_TIME = 5000;
    private final int MAX_REQUESTS = 10;

    private final Map<String, RateLimit> apiLimits = new ConcurrentHashMap<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientIp = request.getRemoteAddr();
        long currentTime = System.currentTimeMillis();
        apiLimits.entrySet().removeIf(entry -> currentTime - entry.getValue().windowStart > WINDOW_TIME);
        RateLimit limit = apiLimits.computeIfAbsent(clientIp, k -> new RateLimit(currentTime, 0));

        if (currentTime - limit.windowStart > WINDOW_TIME) {
            limit.windowStart = currentTime;
            limit.count = 1;
        } else {
            limit.count++;
            if (limit.count > MAX_REQUESTS) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.getWriter().write("API Rate limit exceeded: Maximum 10 requests per 5 seconds allowed.");
                return false;
            }
        }
        return true;
    }
    private static class RateLimit {
        long windowStart;
        int count;
        public RateLimit(long windowStart, int count) {
            this.windowStart = windowStart;
            this.count = count;
        }
    }
}