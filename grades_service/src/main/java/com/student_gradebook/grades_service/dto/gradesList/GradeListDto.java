package com.student_gradebook.grades_service.dto.gradesList;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class GradeListDto {
    @NotNull
    private String title;

    @NotNull
    private String courseUnitId;

    @NotNull
    private Integer groupNumber;

    @NotNull
    @NotEmpty
    Set<GradeListItem> list;
}
