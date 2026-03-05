package com.student_gradebook.grades_service.service.client;

import com.student_gradebook.grades_service.entity.Term;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GradesFeignFallback implements GradesFeignClient {
    @Override
    public Term fetchActiveTerm() {
        return null;
    }

    @Override
    public Boolean canGrade(String courseUnitId, String studentId) {
        return null;
    }

    @Override
    public List<String> fetchStudentIds(String courseUnitId, Integer groupNumber) {
        return null;
    }

    @Override
    public String canGradeByStudentNumber(String courseUnitId, String studentNumber) {
        return null;
    }
}
