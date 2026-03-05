package com.student_gradebook.grades_service.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class GradeViewDto {
    private UUID id;
    private String title;
    private String studentId;
    private String courseUnitId;
    private String termId;
    private String grade;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdated;
}
