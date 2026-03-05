package com.student_gradebook.auth_server.service;

import com.student_gradebook.auth_server.dto.OAuthCallbackResult;
import org.springframework.security.oauth2.jwt.Jwt;

public interface OAuthService {
    String initiateLogin();
    OAuthCallbackResult handleCallback(String oauthToken, String oauthVerifier);
    void logOut(Jwt jwt);
}