package com.student_gradebook.auth_server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OAuthCallbackResult {
    private final boolean success;
    private final String jwt;
    private final String errorCode;

    public static OAuthCallbackResult success(String jwt) {
        return new OAuthCallbackResult(true, jwt, null);
    }

    public static OAuthCallbackResult error(String errorCode) {
        return new OAuthCallbackResult(false, null, errorCode);
    }
}