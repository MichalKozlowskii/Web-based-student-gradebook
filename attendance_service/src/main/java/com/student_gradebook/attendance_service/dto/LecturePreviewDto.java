package com.student_gradebook.attendance_service.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
public class LecturePreviewDto {
    private UUID id;
    private String courseUnitId;
    private Integer groupNumber;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdated;
}
