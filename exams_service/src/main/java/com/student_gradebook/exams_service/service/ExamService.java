package com.student_gradebook.exams_service.service;

import com.student_gradebook.exams_service.dto.ExamDto;
import com.student_gradebook.exams_service.dto.ScanResultDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface ExamService {
    UUID createExam(ExamDto examDto, String lecturerId);

    ExamDto fetchExam(UUID examId, String lecturerId);

    List<ExamDto> fetchExams(String lecturerId);

    void updateExam(UUID examId, ExamDto examDto, String lecturerId);

    void deleteExam(UUID examId, String lecturerId);

    void gradeExam(UUID examId, String lecturerId, ScanResultDto scanResultDto);
}
