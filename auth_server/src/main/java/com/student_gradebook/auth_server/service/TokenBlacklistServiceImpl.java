    package com.student_gradebook.auth_server.service;

    import com.student_gradebook.auth_server.security.JwtUtil;
    import lombok.RequiredArgsConstructor;
    import org.springframework.data.redis.core.RedisTemplate;
    import org.springframework.stereotype.Service;

    import java.util.concurrent.TimeUnit;

    @Service
    @RequiredArgsConstructor
    public class TokenBlacklistServiceImpl implements TokenBlacklistService {
        private final RedisTemplate<String, String> redisTemplate;
        private final JwtUtil jwtUtil;

        @Override
        public void blackListToken(String jwt) {
            String key = "blacklist:" + jwt;
            long ttl = jwtUtil.extractExpiration(jwt).getTime() - System.currentTimeMillis();

            if (ttl > 0) {
                redisTemplate.opsForValue().set(
                        key,
                        "revoked",
                        ttl,
                        TimeUnit.MILLISECONDS
                );
            }
        }
    }
