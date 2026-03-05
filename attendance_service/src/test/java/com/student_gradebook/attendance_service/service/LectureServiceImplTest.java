package com.student_gradebook.attendance_service.service;

import com.student_gradebook.attendance_service.controller.exceptions.NoResponseFromApiException;
import com.student_gradebook.attendance_service.controller.exceptions.UnAuthorizedActionException;
import com.student_gradebook.attendance_service.dto.AttendanceDto;
import com.student_gradebook.attendance_service.dto.LectureDto;
import com.student_gradebook.attendance_service.dto.LecturePreviewDto;
import com.student_gradebook.attendance_service.dto.LectureUpdateDto;
import com.student_gradebook.attendance_service.entity.Attendance;
import com.student_gradebook.attendance_service.entity.Lecture;
import com.student_gradebook.attendance_service.entity.Term;
import com.student_gradebook.attendance_service.enums.Status;
import com.student_gradebook.attendance_service.repository.LectureRepository;
import com.student_gradebook.attendance_service.service.client.AttendanceFeignClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LectureServiceImplTest {

    @Mock
    TermService termService;

    @Mock
    AttendanceFeignClient attendanceFeignClient;

    @Mock
    LectureRepository lectureRepository;

    @InjectMocks
    LectureServiceImpl lectureService;

    Term activeTerm;

    @BeforeEach
    void setup() {
        activeTerm = Term.builder().id("TERM_1").build();
    }

    @Test
    void createNewLecture_success_missingAttendanceDefaultsToAbsent() {
        when(attendanceFeignClient.fetchStudentIds("CU_1", 1))
                .thenReturn(List.of("S1", "S2"));

        LectureDto dto = LectureDto.builder()
                .courseUnitId("CU_1")
                .groupNumber(1)
                .attendanceList(Set.of(
                        AttendanceDto.builder()
                                .studentId("S1")
                                .status(Status.PRESENT)
                                .build()
                ))
                .build();

        when(termService.findActiveTerm()).thenReturn(activeTerm);
        lectureService.createNewLecture(dto);

        ArgumentCaptor<Lecture> captor = ArgumentCaptor.forClass(Lecture.class);
        verify(lectureRepository).save(captor.capture());

        Lecture saved = captor.getValue();
        assertEquals(2, saved.getAttendanceList().size());

        Map<String, Status> statusMap = new HashMap<>();
        saved.getAttendanceList()
                .forEach(a -> statusMap.put(a.getStudentId(), a.getStatus()));

        assertEquals(Status.PRESENT, statusMap.get("S1"));
        assertEquals(Status.ABSENT, statusMap.get("S2"));
    }

    @Test
    void createNewLecture_noResponseFromApi() {
        when(attendanceFeignClient.fetchStudentIds("CU_1", 1)).thenReturn(null);

        LectureDto dto = LectureDto.builder()
                .courseUnitId("CU_1")
                .groupNumber(1)
                .attendanceList(Set.of())
                .build();

        assertThrows(NoResponseFromApiException.class,
                () -> lectureService.createNewLecture(dto));
    }

    @Test
    void fetchLectures_success() {
        when(attendanceFeignClient.isAuthorisedInGroup("CU_1", 1)).thenReturn(true);

        Lecture lecture = Lecture.builder()
                .id(UUID.randomUUID())
                .courseUnitId("CU_1")
                .groupNumber(1)
                .termId("TERM_1")
                .attendanceList(Set.of())
                .build();

        when(lectureRepository.findAllByCourseUnitIdAndGroupNumberAndTermId(
                "CU_1", 1, "TERM_1"))
                .thenReturn(List.of(lecture));
        when(termService.findActiveTerm()).thenReturn(activeTerm);

        List<LecturePreviewDto> result =
                lectureService.fetchLectures("CU_1", 1);

        assertEquals(1, result.size());
        assertEquals("CU_1", result.get(0).getCourseUnitId());
    }

    @Test
    void fetchLectures_unauthorized() {
        when(attendanceFeignClient.isAuthorisedInGroup("CU_1", 1)).thenReturn(false);

        assertThrows(UnAuthorizedActionException.class,
                () -> lectureService.fetchLectures("CU_1", 1));
    }

    @Test
    void fetchLecture_notFound() {
        when(lectureRepository.findById(any())).thenReturn(Optional.empty());

        LectureDto result = lectureService.fetchLecture(UUID.randomUUID());

        assertNull(result);
    }

    @Test
    void deleteLecture_success() {
        Lecture lecture = Lecture.builder()
                .id(UUID.randomUUID())
                .courseUnitId("CU_1")
                .groupNumber(1)
                .build();

        when(lectureRepository.findById(lecture.getId()))
                .thenReturn(Optional.of(lecture));
        when(attendanceFeignClient.isAuthorisedInGroup("CU_1", 1))
                .thenReturn(true);

        boolean result = lectureService.deleteLecture(lecture.getId());

        assertTrue(result);
        verify(lectureRepository).deleteById(lecture.getId());
    }

    @Test
    void updateLecture_partialAttendanceUpdate() {
        Attendance a1 = Attendance.builder()
                .studentId("S1")
                .status(Status.ABSENT)
                .build();

        Attendance a2 = Attendance.builder()
                .studentId("S2")
                .status(Status.ABSENT)
                .build();

        Lecture lecture = Lecture.builder()
                .courseUnitId("CU_1")
                .groupNumber(1)
                .attendanceList(Set.of(a1, a2))
                .build();

        when(lectureRepository.findById(any()))
                .thenReturn(Optional.of(lecture));
        when(attendanceFeignClient.isAuthorisedInGroup("CU_1", 1))
                .thenReturn(true);

        LectureUpdateDto updateDto = LectureUpdateDto.builder()
                .attendanceList(Set.of(
                        AttendanceDto.builder()
                                .studentId("S1")
                                .status(Status.PRESENT)
                                .build()
                ))
                .build();

        boolean result = lectureService.updateLecture(UUID.randomUUID(), updateDto);

        assertTrue(result);
        assertEquals(Status.PRESENT, a1.getStatus());
        assertEquals(Status.ABSENT, a2.getStatus());
        verify(lectureRepository).save(lecture);
    }

    @Test
    void updateLecture_notFound() {
        when(lectureRepository.findById(any())).thenReturn(Optional.empty());

        boolean result = lectureService.updateLecture(UUID.randomUUID(),
                LectureUpdateDto.builder().attendanceList(Set.of()).build());

        assertFalse(result);
    }
}
