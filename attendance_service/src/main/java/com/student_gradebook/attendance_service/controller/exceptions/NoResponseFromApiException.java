package com.student_gradebook.attendance_service.controller.exceptions;

public class NoResponseFromApiException extends RuntimeException {
    public NoResponseFromApiException(String message) {
        super(message);
    }
}