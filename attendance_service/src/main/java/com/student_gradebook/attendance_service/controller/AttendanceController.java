package com.student_gradebook.attendance_service.controller;

import com.student_gradebook.attendance_service.dto.AttendanceDto;
import com.student_gradebook.attendance_service.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;

    @GetMapping("/fetch/{courseUnitId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<AttendanceDto>> fetchStudentsAttendance(@AuthenticationPrincipal Jwt jwt,
                                                                       @PathVariable("courseUnitId") String courseUnitId) {
        String studentId = jwt.getClaimAsString("id");
        List<AttendanceDto> attendanceList = attendanceService.fetchStudentsAttendance(studentId, courseUnitId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(attendanceList);
    }

    @GetMapping("/fetch/{courseUnitId}/{groupNumber}")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<Map<String, List<AttendanceDto>>> fetchAttendanceInGroup(@PathVariable("courseUnitId") String courseUnitId,
                                                                                   @PathVariable("groupNumber") Integer groupNumber) {
        Map<String, List<AttendanceDto>> attendanceMap = attendanceService
                .fetchAttendanceInGroup(courseUnitId, groupNumber);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(attendanceMap);
    }
}
