package com.swiftcart.service;

import io.github.bucket4j.*;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RateLimiterService {
    
    @Value("${rate-limiter.token-capacity}")
    private int tokenCapacity;
    @Value("${rate-limiter.duration}")
    private int durationSeconds;

    private final ProxyManager<String> proxyManager;

    public Bucket resolveBucket(String key) {
        BucketConfiguration configuration = BucketConfiguration.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(tokenCapacity)
                        .refillGreedy(tokenCapacity, Duration.ofSeconds(durationSeconds))
                        .build())
                .build();

        return proxyManager.builder().build(key, () -> configuration);
    }
}
