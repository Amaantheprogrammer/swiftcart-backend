package com.swiftcart.interceptor;

import com.swiftcart.entity.User;
import com.swiftcart.service.RateLimiterService;
import com.swiftcart.util.CurrentUserUtil;
import io.github.bucket4j.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {
    private final RateLimiterService rateLimiterService;
    private final CurrentUserUtil currentUserUtil;

    private static final long NANOS_PER_SECOND = 1_000_000_000L;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String identifier = resolveIdentifier(request);
        Bucket bucket = rateLimiterService.resolveBucket(identifier);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        response.setHeader( "X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()) );
        if (probe.isConsumed()) {
            return true;
        }
        long nanosToWait = probe.getNanosToWaitForRefill();
        // Round up so Retry-After does not underestimate the wait.
        long retryAfterSeconds = nanosToWait / NANOS_PER_SECOND;
        if (nanosToWait % NANOS_PER_SECOND != 0) {
            retryAfterSeconds++;
        }
        // Ensure Retry-After is at least one second.
        retryAfterSeconds = Math.max(1, retryAfterSeconds);
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));
        response.setHeader("Cache-Control", "no-store");
        response.getWriter().write( "{\"message\":\"Rate limit exceeded. Please try again in " + retryAfterSeconds + " seconds.\"}" );
        return false;
    }

    private String resolveIdentifier(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            User user = currentUserUtil.getCurrentUser();
            if (user != null && user.getId() != null) {
                return "rate_limit:user_" + user.getId();
            }
            throw new IllegalStateException("Authenticated user could not be resolved for rate limiting.");
        }
        return "rate_limit:ip_" + getClientIp(request);
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        return ip == null || ip.isBlank() ? "unknown" : ip;
    }
}
