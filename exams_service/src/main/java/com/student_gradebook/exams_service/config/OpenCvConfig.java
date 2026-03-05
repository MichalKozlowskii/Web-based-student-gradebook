package com.student_gradebook.exams_service.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenCvConfig {
    static {
        nu.pattern.OpenCV.loadShared();
    }
}
