package com.student_gradebook.grades_service.service;

import com.student_gradebook.grades_service.dto.ExamGradeCreationDto;
import com.student_gradebook.grades_service.dto.GradeCreationDto;
import com.student_gradebook.grades_service.dto.GradeEditionDto;
import com.student_gradebook.grades_service.dto.GradeViewDto;
import com.student_gradebook.grades_service.dto.gradesList.GradeListDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface GradeService {
    void addGrade(GradeCreationDto gradeCreationDto);
    void addGradeFromExam(ExamGradeCreationDto examGradeCreationDto);
    Boolean addGradesFromList(GradeListDto gradeListDto);
    List<GradeViewDto> fetchStudentGrades(String studentId, String courseUnitId);
    List<GradeViewDto> fetchLastStudentGrades(String studentId);
    Map<String, List<GradeViewDto>> fetchGradesInGroup(String courseUnitId, Integer groupNumber);
    Boolean updateGrade(UUID gradeId, GradeEditionDto gradeEditionDto);
    Boolean deleteGrade(UUID gradeId);
}
