package com.myProject.config;

import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.Bucket4jLettuce;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
@RequiredArgsConstructor
public class RateLimiterConfig {

    @Bean(destroyMethod = "close")
    public StatefulRedisConnection<String, byte[]> rateLimitConnection(LettuceConnectionFactory connectionFactory) {
        RedisClient client = (RedisClient) connectionFactory.getRequiredNativeClient();
        return client.connect(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE));
    }

    @Bean
    public ProxyManager<String> proxyManager(StatefulRedisConnection<String, byte[]> connection) {
        return Bucket4jLettuce.casBasedBuilder(connection).build();
    }
}