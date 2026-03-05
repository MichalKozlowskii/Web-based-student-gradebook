package com.student_gradebook.summary_service.service;

import com.student_gradebook.summary_service.controller.exceptions.NoResponseFromApiException;
import com.student_gradebook.summary_service.dto.StudentSummaryDto;
import com.student_gradebook.summary_service.enums.Status;
import com.student_gradebook.summary_service.records.AttendanceRecord;
import com.student_gradebook.summary_service.records.GradeRecord;
import com.student_gradebook.summary_service.service.client.AttendanceFeignClient;
import com.student_gradebook.summary_service.service.client.GradesFeignClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SummaryServiceImplTest {

    @Mock
    private GradesFeignClient gradesFeignClient;

    @Mock
    private AttendanceFeignClient attendanceFeignClient;

    @InjectMocks
    private SummaryServiceImpl summaryService;

    // -------------------------------------------------------------------------
    // getStudentSummary
    // -------------------------------------------------------------------------

    @Test
    void getStudentSummary_shouldThrow_whenGradesResponseIsNull() {
        when(gradesFeignClient.fetchStudentsGrades("CU1")).thenReturn(null);

        assertThrows(NoResponseFromApiException.class,
                () -> summaryService.getStudentSummary("CU1"));

        verifyNoInteractions(attendanceFeignClient);
    }

    @Test
    void getStudentSummary_shouldThrow_whenAttendanceResponseIsNull() {
        when(gradesFeignClient.fetchStudentsGrades("CU1")).thenReturn(List.of());
        when(attendanceFeignClient.fetchStudentsAttendance("CU1")).thenReturn(null);

        assertThrows(NoResponseFromApiException.class,
                () -> summaryService.getStudentSummary("CU1"));
    }

    @Test
    void getStudentSummary_shouldReturnZeroes_whenBothListsAreEmpty() {
        when(gradesFeignClient.fetchStudentsGrades("CU1")).thenReturn(List.of());
        when(attendanceFeignClient.fetchStudentsAttendance("CU1")).thenReturn(List.of());

        StudentSummaryDto result = summaryService.getStudentSummary("CU1");

        assertEquals(0.0, result.getGradesMean());
        assertEquals(0, result.getPlusCount());
        assertEquals(0, result.getMinusCount());
        assertEquals(0, result.getPresent());
        assertEquals(0, result.getAbsent());
        assertEquals(0, result.getExcused());
    }

    @Test
    void getStudentSummary_shouldCalculateMeanCorrectly() {
        List<GradeRecord> grades = List.of(
                new GradeRecord("3.0"),
                new GradeRecord("4.0"),
                new GradeRecord("5.0")
        );
        when(gradesFeignClient.fetchStudentsGrades("CU1")).thenReturn(grades);
        when(attendanceFeignClient.fetchStudentsAttendance("CU1")).thenReturn(List.of());

        StudentSummaryDto result = summaryService.getStudentSummary("CU1");

        assertEquals(4.0, result.getGradesMean());
    }

    @Test
    void getStudentSummary_shouldRoundMeanToTwoDecimalPlaces() {
        List<GradeRecord> grades = List.of(
                new GradeRecord("3.0"),
                new GradeRecord("3.0"),
                new GradeRecord("4.0")
        );
        when(gradesFeignClient.fetchStudentsGrades("CU1")).thenReturn(grades);
        when(attendanceFeignClient.fetchStudentsAttendance("CU1")).thenReturn(List.of());

        StudentSummaryDto result = summaryService.getStudentSummary("CU1");

        assertEquals(3.33, result.getGradesMean());
    }

    @Test
    void getStudentSummary_shouldCountPlussesAndMinuses() {
        List<GradeRecord> grades = List.of(
                new GradeRecord("0.1"),   // plus
                new GradeRecord("0.1"),   // plus
                new GradeRecord("0.01"),  // minus
                new GradeRecord("4.0")
        );
        when(gradesFeignClient.fetchStudentsGrades("CU1")).thenReturn(grades);
        when(attendanceFeignClient.fetchStudentsAttendance("CU1")).thenReturn(List.of());

        StudentSummaryDto result = summaryService.getStudentSummary("CU1");

        assertEquals(2, result.getPlusCount());
        assertEquals(1, result.getMinusCount());
        assertEquals(4.0, result.getGradesMean()); // only "4.0" counts toward mean
    }

    @Test
    void getStudentSummary_shouldNotCountPlussesAndMinusesInMean() {
        List<GradeRecord> grades = List.of(
                new GradeRecord("0.1"),
                new GradeRecord("0.01")
        );
        when(gradesFeignClient.fetchStudentsGrades("CU1")).thenReturn(grades);
        when(attendanceFeignClient.fetchStudentsAttendance("CU1")).thenReturn(List.of());

        StudentSummaryDto result = summaryService.getStudentSummary("CU1");

        assertEquals(0.0, result.getGradesMean());
        assertEquals(1, result.getPlusCount());
        assertEquals(1, result.getMinusCount());
    }

    @Test
    void getStudentSummary_shouldCountAttendanceStatuses() {
        List<AttendanceRecord> attendance = List.of(
                new AttendanceRecord(Status.PRESENT),
                new AttendanceRecord(Status.PRESENT),
                new AttendanceRecord(Status.ABSENT),
                new AttendanceRecord(Status.EXCUSED)
        );
        when(gradesFeignClient.fetchStudentsGrades("CU1")).thenReturn(List.of());
        when(attendanceFeignClient.fetchStudentsAttendance("CU1")).thenReturn(attendance);

        StudentSummaryDto result = summaryService.getStudentSummary("CU1");

        assertEquals(2, result.getPresent());
        assertEquals(1, result.getAbsent());
        assertEquals(1, result.getExcused());
    }

    // -------------------------------------------------------------------------
    // getLecturerSummary
    // -------------------------------------------------------------------------

    @Test
    void getLecturerSummary_shouldThrow_whenGradesMapIsNull() {
        when(gradesFeignClient.fetchGradesInGroup("CU1", 1)).thenReturn(null);

        assertThrows(NoResponseFromApiException.class,
                () -> summaryService.getLecturerSummary("CU1", 1));

        verifyNoInteractions(attendanceFeignClient);
    }

    @Test
    void getLecturerSummary_shouldThrow_whenAttendanceMapIsNull() {
        when(gradesFeignClient.fetchGradesInGroup("CU1", 1)).thenReturn(Map.of());
        when(attendanceFeignClient.fetchAttendanceInGroup("CU1", 1)).thenReturn(null);

        assertThrows(NoResponseFromApiException.class,
                () -> summaryService.getLecturerSummary("CU1", 1));
    }

    @Test
    void getLecturerSummary_shouldReturnEmptyMap_whenNoStudents() {
        when(gradesFeignClient.fetchGradesInGroup("CU1", 1)).thenReturn(Map.of());
        when(attendanceFeignClient.fetchAttendanceInGroup("CU1", 1)).thenReturn(Map.of());

        Map<String, StudentSummaryDto> result = summaryService.getLecturerSummary("CU1", 1);

        assertTrue(result.isEmpty());
    }

    @Test
    void getLecturerSummary_shouldReturnSummaryForEachStudent() {
        Map<String, List<GradeRecord>> gradesMap = Map.of(
                "student1", List.of(new GradeRecord("4.0"), new GradeRecord("5.0")),
                "student2", List.of(new GradeRecord("3.0"))
        );
        Map<String, List<AttendanceRecord>> attendanceMap = Map.of(
                "student1", List.of(new AttendanceRecord(Status.PRESENT)),
                "student2", List.of(new AttendanceRecord(Status.ABSENT))
        );

        when(gradesFeignClient.fetchGradesInGroup("CU1", 1)).thenReturn(gradesMap);
        when(attendanceFeignClient.fetchAttendanceInGroup("CU1", 1)).thenReturn(attendanceMap);

        Map<String, StudentSummaryDto> result = summaryService.getLecturerSummary("CU1", 1);

        assertEquals(2, result.size());

        StudentSummaryDto s1 = result.get("student1");
        assertEquals(4.5, s1.getGradesMean());
        assertEquals(1, s1.getPresent());
        assertEquals(0, s1.getAbsent());

        StudentSummaryDto s2 = result.get("student2");
        assertEquals(3.0, s2.getGradesMean());
        assertEquals(0, s2.getPresent());
        assertEquals(1, s2.getAbsent());
    }
}