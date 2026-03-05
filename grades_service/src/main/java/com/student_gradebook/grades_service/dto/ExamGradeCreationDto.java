package com.student_gradebook.grades_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ExamGradeCreationDto {
    @NotNull
    private String courseUnitId;

    @NotNull
    @Pattern(regexp = "^\\d{6}$")
    private String studentNumber;

    @NotNull
    private String title;

    @NotNull
    @Pattern(regexp = "^(?:0\\.01|0\\.1|2\\.[05]|3\\.[05]|4\\.[05]|5\\.0)$")
    private String grade;
}
