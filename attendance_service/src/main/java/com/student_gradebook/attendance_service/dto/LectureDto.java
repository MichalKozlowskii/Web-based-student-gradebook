package com.student_gradebook.attendance_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class LectureDto {
    private UUID id;

    @NotNull
    @NotEmpty
    private String courseUnitId;

    @NotNull
    @Min(1)
    private Integer groupNumber;

    @NotNull
    @NotEmpty
    private Set<AttendanceDto> attendanceList;

    private String termId;

    private LocalDateTime createdAt;

    private LocalDateTime lastUpdated;
}
