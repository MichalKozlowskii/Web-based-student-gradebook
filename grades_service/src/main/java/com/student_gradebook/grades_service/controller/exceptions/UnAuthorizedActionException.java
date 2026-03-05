package com.student_gradebook.grades_service.controller.exceptions;

public class UnAuthorizedActionException extends RuntimeException {
    public UnAuthorizedActionException(String message) {
        super(message);
    }
}
