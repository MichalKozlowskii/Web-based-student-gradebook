package com.student_gradebook.summary_service.service.client;

import com.student_gradebook.summary_service.records.AttendanceRecord;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class AttendanceFeignFallback implements AttendanceFeignClient {
    @Override
    public List<AttendanceRecord> fetchStudentsAttendance(String courseUnitId) {
        return null;
    }

    @Override
    public Map<String, List<AttendanceRecord>> fetchAttendanceInGroup(String courseUnitId, Integer groupNumber) {
        return null;
    }
}
