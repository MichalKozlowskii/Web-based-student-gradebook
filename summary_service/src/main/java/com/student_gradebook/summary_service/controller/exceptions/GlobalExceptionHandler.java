package com.student_gradebook.summary_service.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoResponseFromApiException.class)
    public ResponseEntity<String> handleTermNotFoundException(NoResponseFromApiException ex) {
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(ex.getMessage());
    }

    @ExceptionHandler(UnAuthorizedActionException.class)
    public ResponseEntity<String> handleUnAuthorizedActionException(UnAuthorizedActionException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }
}