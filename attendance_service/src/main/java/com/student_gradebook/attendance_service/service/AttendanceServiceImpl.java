package com.student_gradebook.attendance_service.service;

import com.student_gradebook.attendance_service.controller.exceptions.NoResponseFromApiException;
import com.student_gradebook.attendance_service.controller.exceptions.UnAuthorizedActionException;
import com.student_gradebook.attendance_service.dto.AttendanceDto;
import com.student_gradebook.attendance_service.mappers.AttendanceMapper;
import com.student_gradebook.attendance_service.repository.AttendanceRepository;
import com.student_gradebook.attendance_service.service.client.AttendanceFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final TermService termService;
    private final AttendanceFeignClient attendanceFeignClient;

    private List<String> fetchStudentIdsFromApi(String courseUnitId, Integer groupNumber) {
        List<String> studentIds = attendanceFeignClient.fetchStudentIds(courseUnitId, groupNumber);
        if (studentIds == null) {
            throw new NoResponseFromApiException("Couldn't get response from groups microservice.");
        }
        if (studentIds.isEmpty()) {
            throw new UnAuthorizedActionException("You don't teach this group or group doesn't exist.");
        }

        return studentIds;
    }

    @Override
    public List<AttendanceDto> fetchStudentsAttendance(String studentId, String courseUnitId) {
        return attendanceRepository.findAllByStudentIdAndLecture_CourseUnitIdAndLecture_TermId(studentId, courseUnitId,
                        termService.findActiveTerm().getId())
                .stream()
                .map(AttendanceMapper::attendanceToAttendanceDto)
                .toList();
    }

    @Override
    public Map<String, List<AttendanceDto>> fetchAttendanceInGroup(String courseUnitId, Integer groupNumber) {
        List<String> studentIds = fetchStudentIdsFromApi(courseUnitId, groupNumber);

        Map<String, List<AttendanceDto>> attendanceMap = new HashMap<>();
        String activeTermId = termService.findActiveTerm().getId();

        for (String studentId : studentIds) {
            List<AttendanceDto> studentAttendance = attendanceRepository
                    .findAllByStudentIdAndLecture_CourseUnitIdAndLecture_TermId(studentId, courseUnitId, activeTermId)
                    .stream()
                    .map(AttendanceMapper::attendanceToAttendanceDto)
                    .toList();

            attendanceMap.put(studentId, studentAttendance);
        }

        return attendanceMap;
    }
}
