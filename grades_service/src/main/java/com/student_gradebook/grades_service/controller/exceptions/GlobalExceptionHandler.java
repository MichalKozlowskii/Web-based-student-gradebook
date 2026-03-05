package com.student_gradebook.grades_service.controller.exceptions;

import feign.FeignException;
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

    @ExceptionHandler(UnAuthorizedActionException.class)
    public ResponseEntity<String> handleUnAuthorizedActionException(UnAuthorizedActionException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<String> handleFeignException(FeignException ex) {
        return ResponseEntity
                .status(ex.status())
                .body(ex.contentUTF8());
    }
}