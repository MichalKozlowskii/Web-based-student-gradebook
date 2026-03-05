package com.student_gradebook.exams_service.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

import java.util.TreeMap;

@Data
@Builder
public class ScanResultDto {

    @NotNull
    private Integer numberOfTasks;

    @NotNull
    @NotEmpty
    TreeMap<Integer, Double> result;

    @NotNull
    private Double totalScore;

    @NotNull
    private Integer resultPercent;

    @NotNull
    @Pattern(regexp = "^(?:0\\.01|0\\.1|2\\.[05]|3\\.[05]|4\\.[05]|5\\.0)$")
    private String grade;

    @NotNull
    private String studentNumber;
}