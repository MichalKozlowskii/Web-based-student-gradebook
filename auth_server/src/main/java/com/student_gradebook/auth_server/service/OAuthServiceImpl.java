package com.student_gradebook.auth_server.service;

import com.github.scribejava.core.exceptions.OAuthException;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.oauth.OAuth10aService;
import com.google.common.util.concurrent.RateLimiter;
import com.student_gradebook.auth_server.controller.exceptions.OAuthRateLimitException;
import com.student_gradebook.auth_server.dto.OAuthCallbackResult;
import com.student_gradebook.auth_server.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthServiceImpl implements OAuthService {
    private final OAuth10aService usosService;
    private final UserService userService;
    private final RedisTemplate<String, String> redisTemplate;
    private final RateLimiter loginRateLimiter;
    private final TokenBlacklistService tokenBlacklistService;

    private static final String OAUTH_TOKEN_PREFIX = "oauth:token:";
    private static final String OAUTH_RATE_PREFIX = "oauth:callback:rate:";
    private static final int TOKEN_EXPIRATION_MINUTES = 10;
    private static final int CALLBACK_RATE_LIMIT_SECONDS = 30;

    @Override
    public String initiateLogin() {
        if (!loginRateLimiter.tryAcquire()) {
            throw new OAuthRateLimitException("Login rate limit exceeded");
        }

        try {
            OAuth1RequestToken requestToken = usosService.getRequestToken();
            storeRequestToken(requestToken);
            return usosService.getAuthorizationUrl(requestToken);
        } catch (Exception e) {
            throw new OAuthException("Failed to initiate login", e);
        }
    }

    @Override
    public OAuthCallbackResult handleCallback(String oauthToken, String oauthVerifier) {
        try {
            // Check rate limiting
            if (!checkCallbackRateLimit(oauthToken)) {
                return OAuthCallbackResult.error("rate_limited");
            }

            // Retrieve and validate stored token
            OAuth1RequestToken requestToken = retrieveAndValidateRequestToken(oauthToken);
            if (requestToken == null) {
                return OAuthCallbackResult.error("invalid_or_expired");
            }

            // Exchange for access token
            OAuth1AccessToken accessToken = usosService.getAccessToken(requestToken, oauthVerifier);

            // Find or create user and generate JWT
            User user = userService.findOrCreateUser(accessToken);
            String jwt = userService.generateJwt(user);

            return OAuthCallbackResult.success(jwt);

        } catch (Exception e) {
            return OAuthCallbackResult.error("authentication_failed");
        }
    }

    @Override
    public void logOut(Jwt jwt) {
        tokenBlacklistService.blackListToken(jwt.getTokenValue());

        String userId = jwt.getClaimAsString("id");
        userService.removeAccessToken(userId);
    }

    private void storeRequestToken(OAuth1RequestToken requestToken) {
        String tokenData = requestToken.getToken() + "|" + requestToken.getTokenSecret();
        redisTemplate.opsForValue().set(
                OAUTH_TOKEN_PREFIX + requestToken.getToken(),
                tokenData,
                TOKEN_EXPIRATION_MINUTES,
                TimeUnit.MINUTES
        );
    }

    private boolean checkCallbackRateLimit(String oauthToken) {
        String rateKey = OAUTH_RATE_PREFIX + oauthToken;
        Long attempts = redisTemplate.opsForValue().increment(rateKey);

        if (attempts == null) {
            return false;
        }

        if (attempts == 1) {
            redisTemplate.expire(rateKey, CALLBACK_RATE_LIMIT_SECONDS, TimeUnit.SECONDS);
        }

        return attempts <= 1;
    }

    private OAuth1RequestToken retrieveAndValidateRequestToken(String oauthToken) {
        String key = OAUTH_TOKEN_PREFIX + oauthToken;
        String stored = redisTemplate.opsForValue().get(key);
        redisTemplate.delete(key);

        if (stored == null) {
            return null;
        }

        String[] parts = stored.split("\\|", 2);
        if (parts.length != 2) {
            log.error("Invalid token data format in Redis");
            return null;
        }

        String storedToken = parts[0];
        String storedSecret = parts[1];

        if (!storedToken.equals(oauthToken)) {
            log.error("Token mismatch: expected {}, got {}", storedToken, oauthToken);
            return null;
        }

        return new OAuth1RequestToken(storedToken, storedSecret);
    }
}