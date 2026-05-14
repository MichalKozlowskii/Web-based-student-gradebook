package com.student_gradebook.exams_service.dto;

import com.student_gradebook.exams_service.validator.ValidScope;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.TreeMap;
import java.util.UUID;

@Data
@Builder
public class ExamDto {
    private UUID id;

    @NotNull
    @Length(min = 1, max = 30)
    private String title;

    @NotNull
    private Integer numberOfTasks;

    @ValidScope
    private TreeMap<Integer, Double> scope;

    @NotNull
    private String courseUnitId;

    @NotNull
    @Size(min = 1, max = 10)
    private Integer weight;

    private String lecturerId;

    private LocalDateTime createdAt;

    private LocalDateTime lastUpdated;
}
