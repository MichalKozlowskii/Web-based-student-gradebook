package com.student_gradebook.summary_service.controller.exceptions;

public class UnAuthorizedActionException extends RuntimeException {
    public UnAuthorizedActionException(String message) {
        super(message);
    }
}
