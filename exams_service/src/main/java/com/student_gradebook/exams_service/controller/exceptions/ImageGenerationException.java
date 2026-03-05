package com.student_gradebook.exams_service.controller.exceptions;

public class ImageGenerationException extends RuntimeException {
    public ImageGenerationException(String message) {
        super(message);
    }
}
