package com.student_gradebook.grades_service.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
public class GradeCreationDto {
    @NotNull
    private String courseUnitId;

    @NotNull
    private String studentId;

    @NotNull
    private String title;

    @NotNull
    @Pattern(regexp = "^(?:0\\.01|0\\.1|2\\.[05]|3\\.[05]|4\\.[05]|5\\.0)$")
    private String grade;

    @NotNull
    @Size(min = 1, max = 10)
    private Integer weight;
}
