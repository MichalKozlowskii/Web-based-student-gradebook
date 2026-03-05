package com.student_gradebook.grades_service.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class GradeEditionDto {
    private String title;

    @Pattern(regexp = "^(?:0\\.01|0\\.1|2\\.[05]|3\\.[05]|4\\.[05]|5\\.0)$")
    private String grade;
}
