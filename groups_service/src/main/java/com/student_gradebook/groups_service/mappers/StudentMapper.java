package com.student_gradebook.groups_service.mappers;

import com.student_gradebook.groups_service.dto.StudentDto;
import com.student_gradebook.groups_service.entity.Student;

public class StudentMapper {
    public static StudentDto mapToStudentDto(Student student) {
        return StudentDto.builder()
                .id(student.getId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .studentNumber(student.getStudentNumber())
                .build();
    }
}
