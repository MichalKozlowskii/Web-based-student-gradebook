package com.student_gradebook.exams_service.service;

import com.student_gradebook.exams_service.controller.exceptions.EntityNotFoundException;
import com.student_gradebook.exams_service.controller.exceptions.UnAuthorizedActionException;
import com.student_gradebook.exams_service.dto.ExamDto;
import com.student_gradebook.exams_service.dto.ScanResultDto;
import com.student_gradebook.exams_service.entity.Exam;
import com.student_gradebook.exams_service.repository.ExamRepository;
import com.student_gradebook.exams_service.service.client.ExamsFeignClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamServiceImplTest {

    @Mock
    private ExamRepository examRepository;

    @Mock
    private ExamsFeignClient examsFeignClient;

    @InjectMocks
    private ExamServiceImpl examService;

    // -------------------------------------------------------------------------
    // createExam
    // -------------------------------------------------------------------------

    @Test
    void createExam_shouldSaveExamAndReturnId() {
        ExamDto examDto = ExamDto.builder()
                .title("Midterm")
                .numberOfTasks(10)
                .scope(new TreeMap<>())
                .courseUnitId("CU1")
                .build();

        UUID generatedId = UUID.randomUUID();
        Exam savedExam = Exam.builder().id(generatedId).lecturerId("lecturer1").build();

        when(examRepository.save(any(Exam.class))).thenReturn(savedExam);

        UUID result = examService.createExam(examDto, "lecturer1");

        assertEquals(generatedId, result);
        verify(examRepository).save(argThat(exam -> exam.getLecturerId().equals("lecturer1")));
    }

    // -------------------------------------------------------------------------
    // fetchExam
    // -------------------------------------------------------------------------

    @Test
    void fetchExam_shouldReturnExamDto_whenExamExistsAndLecturerMatches() {
        UUID examId = UUID.randomUUID();
        Exam exam = Exam.builder()
                .id(examId)
                .lecturerId("lecturer1")
                .title("Midterm")
                .build();

        when(examRepository.findById(examId)).thenReturn(Optional.of(exam));

        ExamDto result = examService.fetchExam(examId, "lecturer1");

        assertNotNull(result);
        assertEquals("Midterm", result.getTitle());
    }

    @Test
    void fetchExam_shouldThrowEntityNotFoundException_whenExamNotFound() {
        UUID examId = UUID.randomUUID();
        when(examRepository.findById(examId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> examService.fetchExam(examId, "lecturer1"));
    }

    @Test
    void fetchExam_shouldThrowUnAuthorizedActionException_whenLecturerDoesNotMatch() {
        UUID examId = UUID.randomUUID();
        Exam exam = Exam.builder()
                .id(examId)
                .lecturerId("lecturer1")
                .build();

        when(examRepository.findById(examId)).thenReturn(Optional.of(exam));

        assertThrows(UnAuthorizedActionException.class,
                () -> examService.fetchExam(examId, "otherLecturer"));
    }

    // -------------------------------------------------------------------------
    // fetchExams
    // -------------------------------------------------------------------------

    @Test
    void fetchExams_shouldReturnMappedDtoList() {
        Exam exam1 = Exam.builder().id(UUID.randomUUID()).lecturerId("lecturer1").title("Midterm").build();
        Exam exam2 = Exam.builder().id(UUID.randomUUID()).lecturerId("lecturer1").title("Final").build();

        when(examRepository.findAllByLecturerIdOrderByLastUpdatedDesc("lecturer1"))
                .thenReturn(List.of(exam1, exam2));

        List<ExamDto> result = examService.fetchExams("lecturer1");

        assertEquals(2, result.size());
    }

    @Test
    void fetchExams_shouldReturnEmptyList_whenNoExamsFound() {
        when(examRepository.findAllByLecturerIdOrderByLastUpdatedDesc("lecturer1"))
                .thenReturn(List.of());

        List<ExamDto> result = examService.fetchExams("lecturer1");

        assertTrue(result.isEmpty());
    }

    // -------------------------------------------------------------------------
    // updateExam
    // -------------------------------------------------------------------------

    @Test
    void updateExam_shouldUpdateFieldsAndSave() {
        UUID examId = UUID.randomUUID();
        Exam existing = Exam.builder()
                .id(examId)
                .lecturerId("lecturer1")
                .title("Old Title")
                .build();

        ExamDto updateDto = ExamDto.builder()
                .title("New Title")
                .numberOfTasks(5)
                .scope(new TreeMap<>())
                .courseUnitId("CU2")
                .build();

        when(examRepository.findById(examId)).thenReturn(Optional.of(existing));

        examService.updateExam(examId, updateDto, "lecturer1");

        assertEquals("New Title", existing.getTitle());
        assertEquals(5, existing.getNumberOfTasks());
        assertEquals("CU2", existing.getCourseUnitId());
        verify(examRepository).save(existing);
    }

    @Test
    void updateExam_shouldThrowEntityNotFoundException_whenExamNotFound() {
        UUID examId = UUID.randomUUID();
        when(examRepository.findById(examId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> examService.updateExam(examId, ExamDto.builder().build(), "lecturer1"));
    }

    @Test
    void updateExam_shouldThrowUnAuthorizedActionException_whenLecturerDoesNotMatch() {
        UUID examId = UUID.randomUUID();
        Exam existing = Exam.builder()
                .id(examId)
                .lecturerId("lecturer1")
                .build();

        when(examRepository.findById(examId)).thenReturn(Optional.of(existing));

        assertThrows(UnAuthorizedActionException.class,
                () -> examService.updateExam(examId, ExamDto.builder().build(), "otherLecturer"));

        verify(examRepository, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // deleteExam
    // -------------------------------------------------------------------------

    @Test
    void deleteExam_shouldDeleteExam_whenAuthorized() {
        UUID examId = UUID.randomUUID();
        Exam existing = Exam.builder()
                .id(examId)
                .lecturerId("lecturer1")
                .build();

        when(examRepository.findById(examId)).thenReturn(Optional.of(existing));

        examService.deleteExam(examId, "lecturer1");

        verify(examRepository).deleteById(examId);
    }

    @Test
    void deleteExam_shouldThrowEntityNotFoundException_whenExamNotFound() {
        UUID examId = UUID.randomUUID();
        when(examRepository.findById(examId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> examService.deleteExam(examId, "lecturer1"));

        verify(examRepository, never()).deleteById(any());
    }

    @Test
    void deleteExam_shouldThrowUnAuthorizedActionException_whenLecturerDoesNotMatch() {
        UUID examId = UUID.randomUUID();
        Exam existing = Exam.builder()
                .id(examId)
                .lecturerId("lecturer1")
                .build();

        when(examRepository.findById(examId)).thenReturn(Optional.of(existing));

        assertThrows(UnAuthorizedActionException.class,
                () -> examService.deleteExam(examId, "otherLecturer"));

        verify(examRepository, never()).deleteById(any());
    }

    // -------------------------------------------------------------------------
    // gradeExam
    // -------------------------------------------------------------------------

    @Test
    void gradeExam_shouldCallFeignClientWithCorrectDto() {
        UUID examId = UUID.randomUUID();
        Exam exam = Exam.builder()
                .id(examId)
                .lecturerId("lecturer1")
                .title("Midterm")
                .courseUnitId("CU1")
                .build();

        ScanResultDto scanResultDto = ScanResultDto.builder()
                .grade("5.0")
                .studentNumber("S123")
                .build();

        when(examRepository.findById(examId)).thenReturn(Optional.of(exam));

        examService.gradeExam(examId, "lecturer1", scanResultDto);

        verify(examsFeignClient).addGradeFromExam(argThat(dto ->
                dto.getTitle().equals("Midterm") &&
                        dto.getGrade().equals("5.0") &&
                        dto.getCourseUnitId().equals("CU1") &&
                        dto.getStudentNumber().equals("S123")
        ));
    }

    @Test
    void gradeExam_shouldThrowEntityNotFoundException_whenExamNotFound() {
        UUID examId = UUID.randomUUID();
        when(examRepository.findById(examId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> examService.gradeExam(examId, "lecturer1", ScanResultDto.builder().build()));

        verifyNoInteractions(examsFeignClient);
    }

    @Test
    void gradeExam_shouldThrowUnAuthorizedActionException_whenLecturerDoesNotMatch() {
        UUID examId = UUID.randomUUID();
        Exam exam = Exam.builder()
                .id(examId)
                .lecturerId("lecturer1")
                .build();

        when(examRepository.findById(examId)).thenReturn(Optional.of(exam));

        assertThrows(UnAuthorizedActionException.class,
                () -> examService.gradeExam(examId, "otherLecturer", ScanResultDto.builder().build()));

        verifyNoInteractions(examsFeignClient);
    }
}