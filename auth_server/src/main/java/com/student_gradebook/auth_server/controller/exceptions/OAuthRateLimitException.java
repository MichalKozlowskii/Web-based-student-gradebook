package com.student_gradebook.auth_server.controller.exceptions;

public class OAuthRateLimitException extends OAuthException {
    public OAuthRateLimitException(String message) {
        super(message);
    }
}
