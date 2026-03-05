package com.student_gradebook.auth_server.controller.exceptions;

public class UnsupportedUsosUserException extends RuntimeException {
    public UnsupportedUsosUserException(String message) {
        super(message);
    }
}
