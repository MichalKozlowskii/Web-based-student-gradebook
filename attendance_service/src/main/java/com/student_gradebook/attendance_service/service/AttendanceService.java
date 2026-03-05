package com.student_gradebook.attendance_service.service;

import com.student_gradebook.attendance_service.dto.AttendanceDto;

import java.util.List;
import java.util.Map;

public interface AttendanceService {
    List<AttendanceDto> fetchStudentsAttendance(String studentId, String courseUnitId);

    Map<String, List<AttendanceDto>> fetchAttendanceInGroup(String courseUnitId, Integer groupNumber);
}
