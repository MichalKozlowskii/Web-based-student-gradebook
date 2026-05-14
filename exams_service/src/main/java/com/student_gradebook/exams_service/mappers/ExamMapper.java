package com.student_gradebook.exams_service.mappers;

import com.student_gradebook.exams_service.dto.ExamDto;
import com.student_gradebook.exams_service.entity.Exam;

public class ExamMapper {
    public static Exam examDtoToExam(ExamDto examDto) {
        return Exam.builder()
                .title(examDto.getTitle())
                .courseUnitId(examDto.getCourseUnitId())
                .scope(examDto.getScope())
                .numberOfTasks(examDto.getNumberOfTasks())
                .weight(examDto.getWeight())
                .build();
    }

    public static ExamDto examToExamDto(Exam exam) {
        return ExamDto.builder()
                .id(exam.getId())
                .title(exam.getTitle())
                .courseUnitId(exam.getCourseUnitId())
                .lecturerId(exam.getLecturerId())
                .scope(exam.getScope())
                .numberOfTasks(exam.getNumberOfTasks())
                .weight(exam.getWeight())
                .lastUpdated(exam.getLastUpdated())
                .createdAt(exam.getCreatedAt())
                .build();
    }
}
