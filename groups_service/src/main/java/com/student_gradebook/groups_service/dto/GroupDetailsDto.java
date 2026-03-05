package com.student_gradebook.groups_service.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class GroupDetailsDto {
    private UUID id;
    private String courseUnitId;
    private Integer groupNumber;
    private String courseName;
    private String termId;
    private List<StudentDto> participants;
    private String lecturerId;
}
