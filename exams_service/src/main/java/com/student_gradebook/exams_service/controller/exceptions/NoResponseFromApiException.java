package com.student_gradebook.exams_service.controller.exceptions;

public class NoResponseFromApiException extends RuntimeException {
    public NoResponseFromApiException(String message) {
        super(message);
    }
}