package com.student_gradebook.auth_server.service;

import com.student_gradebook.auth_server.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenBlacklistServiceImplTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private ValueOperations<String, String> valueOps;

    @InjectMocks
    private TokenBlacklistServiceImpl tokenBlacklistService;

    @Test
    void blackListToken_shouldStoreInRedis_whenTokenNotYetExpired() {
        String jwt = "valid.jwt.token";
        long futureTime = System.currentTimeMillis() + 60_000;

        when(jwtUtil.extractExpiration(jwt)).thenReturn(new Date(futureTime));
        when(redisTemplate.opsForValue()).thenReturn(valueOps);

        tokenBlacklistService.blackListToken(jwt);

        verify(valueOps).set(
                eq("blacklist:" + jwt),
                eq("revoked"),
                longThat(ttl -> ttl > 0 && ttl <= 60_000),
                eq(TimeUnit.MILLISECONDS)
        );
    }

    @Test
    void blackListToken_shouldNotStoreInRedis_whenTokenAlreadyExpired() {
        String jwt = "expired.jwt.token";
        long pastTime = System.currentTimeMillis() - 1000;

        when(jwtUtil.extractExpiration(jwt)).thenReturn(new Date(pastTime));

        tokenBlacklistService.blackListToken(jwt);

        verifyNoInteractions(redisTemplate);
    }

    @Test
    void blackListToken_shouldNotStoreInRedis_whenTokenExpiresExactlyNow() {
        String jwt = "just.expired.token";

        when(jwtUtil.extractExpiration(jwt)).thenReturn(new Date(System.currentTimeMillis()));

        tokenBlacklistService.blackListToken(jwt);

        verifyNoInteractions(redisTemplate);
    }
}