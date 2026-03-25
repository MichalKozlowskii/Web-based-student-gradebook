package com.student_gradebook.grades_service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CategoryDto {
    private Long id;

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @Min(1)
    @Max(10)
    private Integer weight;

    private LocalDateTime createdAt;

    private LocalDateTime lastUpdated;
}
