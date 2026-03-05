package com.student_gradebook.attendance_service.service.client;

import com.student_gradebook.attendance_service.entity.Term;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AttendanceFeignFallback implements AttendanceFeignClient{
    @Override
    public Term fetchActiveTerm() {
        return null;
    }

    @Override
    public Boolean isAuthorisedInGroup(String courseUnitId, Integer groupNumber) {
        return null;
    }

    @Override
    public List<String> fetchStudentIds(String courseUnitId, Integer groupNumber) {
        return null;
    }
}
