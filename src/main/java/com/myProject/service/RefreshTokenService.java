package com.myProject.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshExpiration;

    private final RedisTemplate<String, String> redisTemplate;

    public String createRefreshToken(String email) {
        String refreshToken  = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(
                "refresh_token:" + refreshToken,
                email,
                Duration.ofMillis(refreshExpiration)
        );
        return refreshToken;
    }

    public String verifyAndGetEmail(String refreshToken) {
        return redisTemplate.opsForValue().get("refresh_token:" + refreshToken);
    }

    public void deleteRefreshToken(String refreshToken) {
        redisTemplate.delete("refresh_token:" + refreshToken);
    }
}
