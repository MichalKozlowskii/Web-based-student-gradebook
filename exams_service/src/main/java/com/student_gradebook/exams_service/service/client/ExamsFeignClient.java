package com.student_gradebook.exams_service.service.client;

import com.student_gradebook.exams_service.config.FeignJwtConfig;
import com.student_gradebook.exams_service.dto.ExamGradeCreationDto;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "exams",
        url = "http://grades:9000",
        configuration = FeignJwtConfig.class,
        fallback = ExamsFeignFallback.class
)
@Primary
public interface ExamsFeignClient {
    @PostMapping("/api/add/exam")
    ResponseEntity<String> addGradeFromExam(@Valid @RequestBody ExamGradeCreationDto examGradeCreationDto);
}
