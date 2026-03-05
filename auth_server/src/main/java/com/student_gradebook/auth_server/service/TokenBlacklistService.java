package com.student_gradebook.auth_server.service;

import org.springframework.security.oauth2.jwt.Jwt;

public interface TokenBlacklistService {
    void blackListToken(String jwt);
}
