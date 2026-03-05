package com.student_gradebook.summary_service.service.client;

import com.student_gradebook.summary_service.records.GradeRecord;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class GradesFeignFallback implements GradesFeignClient {
    @Override
    public List<GradeRecord> fetchStudentsGrades(String courseUnitId) {
        return null;
    }

    @Override
    public Map<String, List<GradeRecord>> fetchGradesInGroup(String courseUnitId, Integer groupNumber) {
        return null;
    }
}
