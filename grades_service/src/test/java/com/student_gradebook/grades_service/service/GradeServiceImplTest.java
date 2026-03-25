package com.student_gradebook.grades_service.service;

import com.student_gradebook.grades_service.controller.exceptions.NoResponseFromApiException;
import com.student_gradebook.grades_service.controller.exceptions.ResourceNotFoundException;
import com.student_gradebook.grades_service.controller.exceptions.UnAuthorizedActionException;
import com.student_gradebook.grades_service.dto.GradeCreationDto;
import com.student_gradebook.grades_service.dto.GradeEditionDto;
import com.student_gradebook.grades_service.dto.gradesList.GradeListDto;
import com.student_gradebook.grades_service.dto.gradesList.GradeListItem;
import com.student_gradebook.grades_service.entity.Grade;
import com.student_gradebook.grades_service.entity.Term;
import com.student_gradebook.grades_service.repository.GradeRepository;
import com.student_gradebook.grades_service.service.client.GradesFeignClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GradeServiceImplTest {

    @Mock
    private GradeRepository gradeRepository;

    @Mock
    private TermService termService;

    @Mock
    private GradesFeignClient gradesFeignClient;

    @InjectMocks
    private GradeServiceImpl gradeService;

    private Term activeTerm;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        activeTerm = new Term();
        activeTerm.setId("term-1");
        when(termService.findActiveTerm()).thenReturn(activeTerm);
    }

    // --------- addGrade ---------
    @Test
    void addGrade_shouldSaveGrade_whenPermissionGranted() {
        GradeCreationDto dto = GradeCreationDto.builder()
                .studentId("student-1")
                .courseUnitId("course-1")
                .grade("3.0")
                .build();

        when(gradesFeignClient.canGrade(dto.getCourseUnitId(), dto.getStudentId())).thenReturn(true);

        gradeService.addGrade(dto);

        ArgumentCaptor<Grade> captor = ArgumentCaptor.forClass(Grade.class);
        verify(gradeRepository).save(captor.capture());
        Grade saved = captor.getValue();

        assertEquals(dto.getStudentId(), saved.getStudentId());
        assertEquals(dto.getCourseUnitId(), saved.getCourseUnitId());
        assertEquals(dto.getGrade(), saved.getGrade());
        assertEquals(activeTerm.getId(), saved.getTermId());
    }

    @Test
    void addGrade_shouldThrow_whenNoPermission() {
        GradeCreationDto dto = GradeCreationDto.builder()
                .studentId("student-1")
                .courseUnitId("course-1")
                .grade("3.0")
                .build();

        when(gradesFeignClient.canGrade(dto.getCourseUnitId(), dto.getStudentId())).thenReturn(false);

        assertThrows(UnAuthorizedActionException.class, () -> gradeService.addGrade(dto));
        verify(gradeRepository, never()).save(any());
    }

    @Test
    void addGrade_shouldThrow_whenApiReturnsNull() {
        GradeCreationDto dto = GradeCreationDto.builder()
                .studentId("student-1")
                .courseUnitId("course-1")
                .grade("3.0")
                .build();

        when(gradesFeignClient.canGrade(dto.getCourseUnitId(), dto.getStudentId())).thenReturn(null);

        assertThrows(NoResponseFromApiException.class, () -> gradeService.addGrade(dto));
        verify(gradeRepository, never()).save(any());
    }

    // --------- addGradesFromList ---------
    @Test
    void addGradesFromList_shouldSaveGrades_whenStudentsExist() {
        GradeListItem item1 = GradeListItem.builder()
                .studentId("student-1")
                .grade("3.0")
                .build();

        GradeListItem item2 = GradeListItem.builder()
                .studentId("student-2")
                .grade("4.0")
                .build();

        GradeListDto dto = GradeListDto.builder()
                .courseUnitId("course-1")
                .groupNumber(1)
                .list(Set.of(item1, item2))
                .build();

        when(gradesFeignClient.fetchStudentIds("course-1", 1))
                .thenReturn(List.of("student-1", "student-2"));

        boolean result = gradeService.addGradesFromList(dto);

        assertTrue(result);
        verify(gradeRepository, times(1)).saveAll(any());
    }

    @Test
    void addGradesFromList_shouldSkipStudentsNotInGroup() {
        GradeListItem item1 = GradeListItem.builder()
                .studentId("student-1")
                .grade("3.0")
                .build();

        GradeListItem item2 = GradeListItem.builder()
                .studentId("student-3") // not in group
                .grade("4.0")
                .build();

        GradeListDto dto = GradeListDto.builder()
                .courseUnitId("course-1")
                .groupNumber(1)
                .list(Set.of(item1, item2))
                .build();

        when(gradesFeignClient.fetchStudentIds("course-1", 1))
                .thenReturn(List.of("student-1", "student-2"));

        boolean result = gradeService.addGradesFromList(dto);

        assertTrue(result);
        verify(gradeRepository, times(1)).saveAll(any());
    }

    @Test
    void addGradesFromList_shouldThrow_whenApiReturnsNull() {
        GradeListDto dto = GradeListDto.builder()
                .courseUnitId("course-1")
                .groupNumber(1)
                .list(Set.of())
                .build();

        when(gradesFeignClient.fetchStudentIds("course-1", 1)).thenReturn(null);

        assertThrows(NoResponseFromApiException.class, () -> gradeService.addGradesFromList(dto));
        verify(gradeRepository, never()).saveAll(any());
    }

    @Test
    void addGradesFromList_shouldThrow_whenEmptyGroup() {
        GradeListItem item1 = GradeListItem.builder()
                .studentId("student-1")
                .grade("3.0")
                .build();

        GradeListDto dto = GradeListDto.builder()
                .courseUnitId("course-1")
                .groupNumber(1)
                .list(Set.of(item1))
                .build();

        when(gradesFeignClient.fetchStudentIds("course-1", 1)).thenReturn(Collections.emptyList());

        assertThrows(UnAuthorizedActionException.class, () -> gradeService.addGradesFromList(dto));
        verify(gradeRepository, never()).saveAll(any());
    }

    // --------- fetchStudentGrades ---------
    @Test
    void fetchStudentGrades_shouldReturnMappedGrades() {
        Grade grade = Grade.builder()
                .studentId("student-1")
                .courseUnitId("course-1")
                .termId(activeTerm.getId())
                .grade("3.0")
                .build();

        when(gradeRepository.findAllByCourseUnitIdAndStudentIdAndTermId("course-1", "student-1", activeTerm.getId()))
                .thenReturn(List.of(grade));

        var result = gradeService.fetchStudentGrades("student-1", "course-1");

        assertEquals(1, result.size());
        assertEquals("3.0", result.get(0).getGrade());
    }

    // --------- fetchGradesInGroup ---------
    @Test
    void fetchGradesInGroup_shouldReturnMapOfGrades() {
        when(gradesFeignClient.fetchStudentIds("course-1", 1)).thenReturn(List.of("student-1"));

        Grade grade = Grade.builder()
                .studentId("student-1")
                .courseUnitId("course-1")
                .termId(activeTerm.getId())
                .grade("4.0")
                .build();

        when(gradeRepository.findAllByCourseUnitIdAndStudentIdAndTermId("course-1", "student-1", activeTerm.getId()))
                .thenReturn(List.of(grade));

        var result = gradeService.fetchGradesInGroup("course-1", 1);

        assertTrue(result.containsKey("student-1"));
        assertEquals(1, result.get("student-1").size());
        assertEquals("4.0", result.get("student-1").get(0).getGrade());
    }

    // --------- updateGrade ---------
    @Test
    void updateGrade_shouldUpdateGrade_whenExistsAndPermissionGranted() {
        UUID gradeId = UUID.randomUUID();
        Grade existing = Grade.builder()
                .id(gradeId)
                .studentId("student-1")
                .courseUnitId("course-1")
                .grade("3.0")
                .termId(activeTerm.getId())
                .build();

        when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(existing));
        when(gradesFeignClient.canGrade("course-1", "student-1")).thenReturn(true);

        GradeEditionDto dto = new GradeEditionDto();
        dto.setGrade("4.0");

        gradeService.updateGrade(gradeId, dto);

        assertEquals("4.0", existing.getGrade());
        verify(gradeRepository).save(existing);
    }

    @Test
    void updateGrade_shouldReturnFalse_whenGradeDoesNotExist() {
        UUID gradeId = UUID.randomUUID();
        when(gradeRepository.findById(gradeId)).thenReturn(Optional.empty());

        GradeEditionDto dto = new GradeEditionDto();
        dto.setGrade("4.0");

        assertThrows(ResourceNotFoundException.class, () -> gradeService.updateGrade(gradeId, dto));
        verify(gradeRepository, never()).save(any());
    }

    @Test
    void updateGrade_shouldThrow_whenNoPermission() {
        UUID gradeId = UUID.randomUUID();
        Grade existing = Grade.builder()
                .id(gradeId)
                .studentId("student-1")
                .courseUnitId("course-1")
                .grade("3.0")
                .termId(activeTerm.getId())
                .build();

        when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(existing));
        when(gradesFeignClient.canGrade("course-1", "student-1")).thenReturn(false);

        GradeEditionDto dto = new GradeEditionDto();
        dto.setGrade("4.0");

        assertThrows(UnAuthorizedActionException.class, () -> gradeService.updateGrade(gradeId, dto));
        verify(gradeRepository, never()).save(any());
    }

    // --------- deleteGrade ---------
    @Test
    void deleteGrade_shouldDelete_whenExistsAndPermissionGranted() {
        UUID gradeId = UUID.randomUUID();
        Grade existing = Grade.builder()
                .id(gradeId)
                .studentId("student-1")
                .courseUnitId("course-1")
                .grade("3.0")
                .termId(activeTerm.getId())
                .build();

        when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(existing));
        when(gradesFeignClient.canGrade("course-1", "student-1")).thenReturn(true);

        gradeService.deleteGrade(gradeId);

        verify(gradeRepository).delete(existing);
    }

    @Test
    void deleteGrade_shouldReturnFalse_whenGradeDoesNotExist() {
        UUID gradeId = UUID.randomUUID();
        when(gradeRepository.findById(gradeId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> gradeService.deleteGrade(gradeId));
        verify(gradeRepository, never()).delete(any());
    }

    @Test
    void deleteGrade_shouldThrow_whenNoPermission() {
        UUID gradeId = UUID.randomUUID();
        Grade existing = Grade.builder()
                .id(gradeId)
                .studentId("student-1")
                .courseUnitId("course-1")
                .grade("3.0")
                .termId(activeTerm.getId())
                .build();

        when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(existing));
        when(gradesFeignClient.canGrade("course-1", "student-1")).thenReturn(false);

        assertThrows(UnAuthorizedActionException.class, () -> gradeService.deleteGrade(gradeId));
        verify(gradeRepository, never()).delete(any());
    }
}

