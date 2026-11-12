package com.swiftcart.service;

import io.github.bucket4j.*;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RateLimiterService {
    private final ProxyManager<String> proxyManager;

    public Bucket resolveBucket(String key) {
        BucketConfiguration configuration = BucketConfiguration.builder()
                .addLimit(Bandwidth.builder()
                        .capacity(20)
                        .refillGreedy(20, Duration.ofSeconds(60))
                        .build())
                .build();

        return proxyManager.builder().build(key, () -> configuration);
    }
}
