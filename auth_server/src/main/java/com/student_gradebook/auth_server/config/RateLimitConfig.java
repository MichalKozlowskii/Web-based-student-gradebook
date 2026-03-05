package com.student_gradebook.auth_server.config;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimitConfig {
    @Bean
    public RateLimiter loginRateLimiter() {
        return RateLimiter.create(10.0); // 10 request per second
    }
}