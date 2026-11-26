package com.swiftcart.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // Maintain five core worker threads
        executor.setMaxPoolSize(10); // Allow up to ten worker threads
        executor.setQueueCapacity(100); // Queue up to 100 waiting tasks
        executor.setThreadNamePrefix("SwiftCart-Async-");
        executor.initialize();
        return executor;
    }
}
