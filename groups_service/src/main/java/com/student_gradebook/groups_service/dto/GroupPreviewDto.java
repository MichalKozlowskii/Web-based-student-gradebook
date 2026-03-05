package com.student_gradebook.groups_service.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class GroupPreviewDto {
    private UUID id;
    private String courseName;
    private String courseUnitId;
    private Integer groupNumber;
}
