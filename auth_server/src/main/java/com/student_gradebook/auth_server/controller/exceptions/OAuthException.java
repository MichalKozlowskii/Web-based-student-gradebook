package com.student_gradebook.auth_server.controller.exceptions;

public class OAuthException extends RuntimeException {
    public OAuthException(String message) {
        super(message);
    }

    public OAuthException(String message, Throwable cause) {
        super(message, cause);
    }
}