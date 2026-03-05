package com.student_gradebook.groups_service.controller.exceptions;

public class NoResponseFromApiException extends RuntimeException {
    public NoResponseFromApiException(String message) {
        super(message);
    }
}
