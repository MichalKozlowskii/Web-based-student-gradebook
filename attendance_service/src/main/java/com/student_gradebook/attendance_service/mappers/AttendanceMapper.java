package com.student_gradebook.attendance_service.mappers;

import com.student_gradebook.attendance_service.dto.AttendanceDto;
import com.student_gradebook.attendance_service.entity.Attendance;

public class AttendanceMapper {
    public static AttendanceDto attendanceToAttendanceDto(Attendance attendance) {
        return AttendanceDto.builder()
                .id(attendance.getId())
                .studentId(attendance.getStudentId())
                .status(attendance.getStatus())
                .lectureId(attendance.getLecture().getId())
                .createdAt(attendance.getCreatedAt())
                .lastUpdated(attendance.getLastUpdated())
                .build();
    }
}
