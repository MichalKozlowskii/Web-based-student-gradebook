package com.student_gradebook.exams_service.controller.exceptions;

public class FileProcessingException extends RuntimeException {
    public FileProcessingException(String message) {
        super(message);
    }
}