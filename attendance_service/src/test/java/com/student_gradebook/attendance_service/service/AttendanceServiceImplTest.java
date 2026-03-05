package com.student_gradebook.attendance_service.service;

import com.student_gradebook.attendance_service.controller.exceptions.NoResponseFromApiException;
import com.student_gradebook.attendance_service.controller.exceptions.UnAuthorizedActionException;
import com.student_gradebook.attendance_service.dto.AttendanceDto;
import com.student_gradebook.attendance_service.entity.Attendance;
import com.student_gradebook.attendance_service.entity.Lecture;
import com.student_gradebook.attendance_service.entity.Term;
import com.student_gradebook.attendance_service.enums.Status;
import com.student_gradebook.attendance_service.repository.AttendanceRepository;
import com.student_gradebook.attendance_service.service.client.AttendanceFeignClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceImplTest {

    @Mock
    AttendanceRepository attendanceRepository;

    @Mock
    TermService termService;

    @Mock
    AttendanceFeignClient attendanceFeignClient;

    @InjectMocks
    AttendanceServiceImpl attendanceService;

    Term activeTerm;
    Attendance attendance;

    @BeforeEach
    void setup() {
        activeTerm = Term.builder()
                .id("TERM_1")
                .build();

        Lecture lecture = Lecture.builder()
                .courseUnitId("CU_1")
                .termId("TERM_1")
                .build();

        attendance = Attendance.builder()
                .id(UUID.randomUUID())
                .studentId("STUDENT_1")
                .status(Status.PRESENT)
                .lecture(lecture)
                .createdAt(LocalDateTime.now())
                .lastUpdated(LocalDateTime.now())
                .build();
    }

    @Test
    void fetchStudentsAttendance_success() {
        when(termService.findActiveTerm()).thenReturn(activeTerm);
        when(attendanceRepository
                .findAllByStudentIdAndLecture_CourseUnitIdAndLecture_TermId(
                        "STUDENT_1", "CU_1", "TERM_1"))
                .thenReturn(List.of(attendance));

        List<AttendanceDto> result =
                attendanceService.fetchStudentsAttendance("STUDENT_1", "CU_1");

        assertEquals(1, result.size());
        assertEquals("STUDENT_1", result.get(0).getStudentId());
        assertEquals(Status.PRESENT, result.get(0).getStatus());
    }

    @Test
    void fetchAttendanceInGroup_success() {
        when(attendanceFeignClient.fetchStudentIds("CU_1", 1))
                .thenReturn(List.of("STUDENT_1", "STUDENT_2"));
        when(termService.findActiveTerm()).thenReturn(activeTerm);
        when(attendanceRepository
                .findAllByStudentIdAndLecture_CourseUnitIdAndLecture_TermId(
                        anyString(), eq("CU_1"), eq("TERM_1")))
                .thenReturn(List.of(attendance));

        Map<String, List<AttendanceDto>> result =
                attendanceService.fetchAttendanceInGroup("CU_1", 1);

        assertEquals(2, result.size());
        assertTrue(result.containsKey("STUDENT_1"));
        assertTrue(result.containsKey("STUDENT_2"));
        assertEquals(1, result.get("STUDENT_1").size());
    }

    @Test
    void fetchAttendanceInGroup_noResponseFromApi() {
        when(attendanceFeignClient.fetchStudentIds("CU_1", 1))
                .thenReturn(null);

        assertThrows(NoResponseFromApiException.class,
                () -> attendanceService.fetchAttendanceInGroup("CU_1", 1));
    }

    @Test
    void fetchAttendanceInGroup_unauthorizedOrEmptyGroup() {
        when(attendanceFeignClient.fetchStudentIds("CU_1", 1))
                .thenReturn(List.of());

        assertThrows(UnAuthorizedActionException.class,
                () -> attendanceService.fetchAttendanceInGroup("CU_1", 1));
    }
}
