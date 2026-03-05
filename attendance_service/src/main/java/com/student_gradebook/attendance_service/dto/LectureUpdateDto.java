package com.student_gradebook.attendance_service.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Builder
@Data
public class LectureUpdateDto {
    @NotNull
    @NotEmpty
    private Set<AttendanceDto> attendanceList;
}
