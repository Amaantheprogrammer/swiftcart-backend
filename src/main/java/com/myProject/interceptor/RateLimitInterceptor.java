package com.myProject.interceptor;

import com.myProject.entity.User;
import com.myProject.service.RateLimiterService;
import com.myProject.util.CurrentUserUtil;
import io.github.bucket4j.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {
    private final RateLimiterService rateLimiterService;
    private final CurrentUserUtil currentUserUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String identifier;
        try {
            User user = currentUserUtil.getCurrentUser();
            identifier = "rate_limit:user_" + user.getId();
        } catch (Exception e) {
            identifier = "rate_limit:ip_" + getClientIp(request);
        }

        Bucket bucket = rateLimiterService.resolveBucket(identifier);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        response.setHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));

        if (probe.isConsumed()) {
            return true;
        }

        long waitForRefillSeconds = probe.getNanosToWaitForRefill() / 1_000_000_000;
        response.setHeader("Retry-After", String.valueOf(waitForRefillSeconds));
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"message\": \"Rate limit exceeded. Please try again in " + waitForRefillSeconds + " seconds.\"}");

        return false;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
