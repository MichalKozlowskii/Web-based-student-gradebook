package com.student_gradebook.groups_service.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoResponseFromApiException.class)
    public ResponseEntity<String> handleNoResponseFromApiException(NoResponseFromApiException ex) {
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).body(ex.getMessage());
    }
}