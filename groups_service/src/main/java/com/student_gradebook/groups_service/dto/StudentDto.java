package com.student_gradebook.groups_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentDto {
    private String id;
    private String firstName;
    private String lastName;
    private String studentNumber;
}
