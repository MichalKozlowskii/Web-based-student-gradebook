package com.student_gradebook.auth_server.service;

import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.oauth.OAuth10aService;
import com.google.common.util.concurrent.RateLimiter;
import com.student_gradebook.auth_server.dto.OAuthCallbackResult;
import com.student_gradebook.auth_server.controller.exceptions.OAuthRateLimitException;
import com.student_gradebook.auth_server.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuthServiceImplTest {

    @Mock
    private OAuth10aService usosService;

    @Mock
    private UserService userService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private RateLimiter loginRateLimiter;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private ValueOperations<String, String> valueOps;

    @InjectMocks
    private OAuthServiceImpl oAuthService;

    // -------------------------------------------------------------------------
    // initiateLogin
    // -------------------------------------------------------------------------

    @Test
    void initiateLogin_shouldReturnAuthorizationUrl_whenRateLimitNotExceeded() throws Exception {
        OAuth1RequestToken requestToken = new OAuth1RequestToken("token123", "secret123");

        when(loginRateLimiter.tryAcquire()).thenReturn(true);
        when(usosService.getRequestToken()).thenReturn(requestToken);
        when(usosService.getAuthorizationUrl(requestToken)).thenReturn("https://usos.example.com/auth?token=token123");
        when(redisTemplate.opsForValue()).thenReturn(valueOps);

        String url = oAuthService.initiateLogin();

        assertEquals("https://usos.example.com/auth?token=token123", url);
        verify(valueOps).set(
                eq("oauth:token:token123"),
                eq("token123|secret123"),
                eq(10L),
                eq(TimeUnit.MINUTES)
        );
    }

    @Test
    void initiateLogin_shouldThrowOAuthRateLimitException_whenRateLimitExceeded() {
        when(loginRateLimiter.tryAcquire()).thenReturn(false);

        assertThrows(OAuthRateLimitException.class, () -> oAuthService.initiateLogin());
        verifyNoInteractions(usosService);
    }

    @Test
    void initiateLogin_shouldThrowOAuthException_whenUsosServiceFails() throws Exception {
        when(loginRateLimiter.tryAcquire()).thenReturn(true);
        when(usosService.getRequestToken()).thenThrow(new RuntimeException("USOS unavailable"));

        assertThrows(Exception.class, () -> oAuthService.initiateLogin());
    }

    // -------------------------------------------------------------------------
    // handleCallback
    // -------------------------------------------------------------------------

    @Test
    void handleCallback_shouldReturnSuccess_whenValidTokenAndVerifier() throws Exception {
        String oauthToken = "token123";
        String oauthVerifier = "verifier123";
        String rateKey = "oauth:callback:rate:" + oauthToken;
        String tokenKey = "oauth:token:" + oauthToken;

        OAuth1AccessToken accessToken = new OAuth1AccessToken("accessToken", "accessSecret");
        User user = User.builder().id("1").build();

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.increment(rateKey)).thenReturn(1L);
        when(valueOps.get(tokenKey)).thenReturn("token123|secret123");
        when(usosService.getAccessToken(any(OAuth1RequestToken.class), eq(oauthVerifier))).thenReturn(accessToken);
        when(userService.findOrCreateUser(accessToken)).thenReturn(user);
        when(userService.generateJwt(user)).thenReturn("jwt_token");

        OAuthCallbackResult result = oAuthService.handleCallback(oauthToken, oauthVerifier);

        assertTrue(result.isSuccess());
        assertEquals("jwt_token", result.getJwt());
    }

    @Test
    void handleCallback_shouldReturnRateLimited_whenTokenAttemptedMoreThanOnce() {
        String oauthToken = "token123";
        String rateKey = "oauth:callback:rate:" + oauthToken;

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.increment(rateKey)).thenReturn(2L);

        OAuthCallbackResult result = oAuthService.handleCallback(oauthToken, "verifier");

        assertFalse(result.isSuccess());
        assertEquals("rate_limited", result.getErrorCode());
        verifyNoInteractions(usosService, userService);
    }

    @Test
    void handleCallback_shouldReturnInvalidOrExpired_whenTokenNotInRedis() {
        String oauthToken = "token123";
        String rateKey = "oauth:callback:rate:" + oauthToken;
        String tokenKey = "oauth:token:" + oauthToken;

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.increment(rateKey)).thenReturn(1L);
        when(valueOps.get(tokenKey)).thenReturn(null);

        OAuthCallbackResult result = oAuthService.handleCallback(oauthToken, "verifier");

        assertFalse(result.isSuccess());
        assertEquals("invalid_or_expired", result.getErrorCode());
    }

    @Test
    void handleCallback_shouldReturnInvalidOrExpired_whenStoredTokenMismatches() {
        String oauthToken = "token123";
        String rateKey = "oauth:callback:rate:" + oauthToken;
        String tokenKey = "oauth:token:" + oauthToken;

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.increment(rateKey)).thenReturn(1L);
        // stored token is different from oauthToken
        when(valueOps.get(tokenKey)).thenReturn("differentToken|secret123");

        OAuthCallbackResult result = oAuthService.handleCallback(oauthToken, "verifier");

        assertFalse(result.isSuccess());
        assertEquals("invalid_or_expired", result.getErrorCode());
    }

    @Test
    void handleCallback_shouldReturnError_whenRateIncrementReturnsNull() {
        String oauthToken = "token123";
        String rateKey = "oauth:callback:rate:" + oauthToken;

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.increment(rateKey)).thenReturn(null);

        OAuthCallbackResult result = oAuthService.handleCallback(oauthToken, "verifier");

        assertFalse(result.isSuccess());
        assertEquals("rate_limited", result.getErrorCode());
    }

    @Test
    void handleCallback_shouldReturnAuthenticationFailed_whenExceptionThrown() throws Exception {
        String oauthToken = "token123";
        String rateKey = "oauth:callback:rate:" + oauthToken;
        String tokenKey = "oauth:token:" + oauthToken;

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.increment(rateKey)).thenReturn(1L);
        when(valueOps.get(tokenKey)).thenReturn("token123|secret123");
        when(usosService.getAccessToken(any(), any())).thenThrow(new RuntimeException("USOS error"));

        OAuthCallbackResult result = oAuthService.handleCallback(oauthToken, "verifier");

        assertFalse(result.isSuccess());
        assertEquals("authentication_failed", result.getErrorCode());
    }

    @Test
    void handleCallback_shouldSetExpiry_onFirstCallbackAttempt() throws Exception {
        String oauthToken = "token123";
        String rateKey = "oauth:callback:rate:" + oauthToken;
        String tokenKey = "oauth:token:" + oauthToken;

        OAuth1AccessToken accessToken = new OAuth1AccessToken("at", "as");
        User user = User.builder().id("1").build();

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.increment(rateKey)).thenReturn(1L);
        when(valueOps.get(tokenKey)).thenReturn("token123|secret123");
        when(usosService.getAccessToken(any(), any())).thenReturn(accessToken);
        when(userService.findOrCreateUser(accessToken)).thenReturn(user);
        when(userService.generateJwt(user)).thenReturn("jwt");

        oAuthService.handleCallback(oauthToken, "verifier");

        verify(redisTemplate).expire(rateKey, 30L, TimeUnit.SECONDS);
    }

    @Test
    void handleCallback_shouldDeleteTokenFromRedis_afterRetrieval() throws Exception {
        String oauthToken = "token123";
        String tokenKey = "oauth:token:" + oauthToken;
        String rateKey = "oauth:callback:rate:" + oauthToken;

        OAuth1AccessToken accessToken = new OAuth1AccessToken("at", "as");
        User user = User.builder().id("1").build();

        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.increment(rateKey)).thenReturn(1L);
        when(valueOps.get(tokenKey)).thenReturn("token123|secret123");
        when(usosService.getAccessToken(any(), any())).thenReturn(accessToken);
        when(userService.findOrCreateUser(accessToken)).thenReturn(user);
        when(userService.generateJwt(user)).thenReturn("jwt");

        oAuthService.handleCallback(oauthToken, "verifier");

        verify(redisTemplate).delete(tokenKey);
    }

    // -------------------------------------------------------------------------
    // logOut
    // -------------------------------------------------------------------------

    @Test
    void logOut_shouldBlacklistTokenAndRemoveAccessToken() {
        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn("raw_jwt_value");
        when(jwt.getClaimAsString("id")).thenReturn("user123");

        oAuthService.logOut(jwt);

        verify(tokenBlacklistService).blackListToken("raw_jwt_value");
        verify(userService).removeAccessToken("user123");
    }
}