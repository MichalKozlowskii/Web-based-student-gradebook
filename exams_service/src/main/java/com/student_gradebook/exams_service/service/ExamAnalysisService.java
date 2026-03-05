package com.student_gradebook.exams_service.service;

import com.student_gradebook.exams_service.dto.ScanResultDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface ExamAnalysisService {
    byte[] addTable(UUID examId, String lecturerId);

    ScanResultDto analyzeExam(MultipartFile image, UUID examId, String lecturerId);
}
