package com.student_gradebook.attendance_service.dto;

import com.student_gradebook.attendance_service.enums.Status;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AttendanceDto {
    private UUID id;

    @NotNull
    @NotEmpty
    private String studentId;

    @NotNull
    @NotEmpty
    private Status status;

    private UUID lectureId;

    private LocalDateTime createdAt;

    private LocalDateTime lastUpdated;
}
