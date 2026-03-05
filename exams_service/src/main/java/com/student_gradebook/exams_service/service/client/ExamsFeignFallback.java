package com.student_gradebook.exams_service.service.client;

import com.student_gradebook.exams_service.controller.exceptions.UnAuthorizedActionException;
import com.student_gradebook.exams_service.dto.ExamGradeCreationDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ExamsFeignFallback implements ExamsFeignClient {
    @Override
    public ResponseEntity<String> addGradeFromExam(ExamGradeCreationDto examGradeCreationDto) {
        throw new UnAuthorizedActionException("Student with this number can't be graded by you or dont exist.");
    }
}
