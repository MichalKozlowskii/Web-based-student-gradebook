package com.student_gradebook.grades_service.mappers;

import com.student_gradebook.grades_service.dto.GradeCreationDto;
import com.student_gradebook.grades_service.dto.GradeViewDto;
import com.student_gradebook.grades_service.entity.Grade;

public class GradeMapper {
    public static GradeViewDto GradeToGradeViewDto(Grade grade) {
        return GradeViewDto.builder()
                .id(grade.getId())
                .title(grade.getTitle())
                .grade(grade.getGrade())
                .courseUnitId(grade.getCourseUnitId())
                .studentId(grade.getStudentId())
                .termId(grade.getTermId())
                .createdAt(grade.getCreatedAt())
                .lastUpdated(grade.getLastUpdated())
                .build();
    }

    public static Grade gradeCreationDtoToGrade(GradeCreationDto gradeCreationDto) {
        return Grade.builder()
                .title(gradeCreationDto.getTitle())
                .courseUnitId(gradeCreationDto.getCourseUnitId())
                .grade(gradeCreationDto.getGrade())
                .studentId(gradeCreationDto.getStudentId())
                .build();
    }
}
