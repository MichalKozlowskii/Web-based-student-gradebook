package com.student_gradebook.grades_service.dto.gradesList;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GradeListItem {
    @NotNull
    private String studentId;

    @NotNull
    @Pattern(regexp = "^(?:2\\.[05]|3\\.[05]|4\\.[05]|5\\.0|0\\.1)$")
    private String grade;
}
