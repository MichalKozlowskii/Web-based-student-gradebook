package com.student_gradebook.groups_service.service;

import com.student_gradebook.groups_service.entity.Student;
import com.student_gradebook.groups_service.records.ParticipantRecord;
import com.student_gradebook.groups_service.records.StudentNumberResponse;
import com.student_gradebook.groups_service.repository.StudentRepository;
import com.student_gradebook.groups_service.service.client.UsosApiFeignClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StudentServiceImplTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private UsosApiFeignClient usosApiFeignClient;

    @InjectMocks
    private StudentServiceImpl studentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findOrSaveParticipants_returnsExistingStudents() {
        ParticipantRecord participant = new ParticipantRecord("1", "John", "Doe", "S123");
        Student existing = Student.builder()
                .id("1")
                .firstName("John")
                .lastName("Doe")
                .studentNumber("S123")
                .build();

        when(studentRepository.findById("1")).thenReturn(Optional.of(existing));

        List<Student> result = studentService.findOrSaveParticipants(List.of(participant));

        assertEquals(1, result.size());
        assertEquals(existing, result.get(0));

        verify(studentRepository).findById("1");
        verify(studentRepository, never()).save(any());
        verifyNoInteractions(usosApiFeignClient);
    }

    @Test
    void findOrSaveParticipants_savesNewStudents() {
        ParticipantRecord participant = new ParticipantRecord("2", "Alice", "Smith", "S456");
        StudentNumberResponse studentNumberResponse = new StudentNumberResponse("S546");
        Student savedStudent = Student.builder()
                .id("2")
                .firstName("Alice")
                .lastName("Smi")
                .studentNumber("S546")
                .build();

        when(studentRepository.findById("2")).thenReturn(Optional.empty());
        when(usosApiFeignClient.fetchStudentNumber("2")).thenReturn(studentNumberResponse);
        when(studentRepository.save(any(Student.class))).thenReturn(savedStudent);

        List<Student> result = studentService.findOrSaveParticipants(List.of(participant));

        assertEquals(1, result.size());
        Student student = result.get(0);
        assertEquals("2", student.getId());
        assertEquals("Alice", student.getFirstName());
        assertEquals("Smi", student.getLastName());
        assertEquals("S546", student.getStudentNumber());

        verify(studentRepository).findById("2");
        verify(usosApiFeignClient).fetchStudentNumber("2");
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void findOrSaveParticipants_mixedExistingAndNewStudents() {
        ParticipantRecord existingRecord = new ParticipantRecord("1", "John", "Doe", "S123");
        ParticipantRecord newRecord = new ParticipantRecord("2", "Alice", "Smith", "S456");

        Student existingStudent = Student.builder()
                .id("1")
                .firstName("John")
                .lastName("Doe")
                .studentNumber("S123")
                .build();
        Student savedNewStudent = Student.builder()
                .id("2")
                .firstName("Alice")
                .lastName("Smi")
                .studentNumber("S456")
                .build();

        StudentNumberResponse studentNumberResponse = new StudentNumberResponse("S456");

        when(studentRepository.findById("1")).thenReturn(Optional.of(existingStudent));
        when(studentRepository.findById("2")).thenReturn(Optional.empty());
        when(usosApiFeignClient.fetchStudentNumber("2")).thenReturn(studentNumberResponse);
        when(studentRepository.save(any(Student.class))).thenReturn(savedNewStudent);

        List<Student> result = studentService.findOrSaveParticipants(List.of(existingRecord, newRecord));

        assertEquals(2, result.size());
        assertEquals(existingStudent, result.get(0));
        assertEquals(savedNewStudent, result.get(1));

        verify(studentRepository).findById("1");
        verify(studentRepository).findById("2");
        verify(usosApiFeignClient).fetchStudentNumber("2");
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void findStudentById_returnsStudentIfExists() {
        Student student = Student.builder()
                .id("1")
                .firstName("John")
                .lastName("Doe")
                .studentNumber("S123")
                .build();

        when(studentRepository.findById("1")).thenReturn(Optional.of(student));

        Student result = studentService.findStudentById("1");
        assertEquals(student, result);

        verify(studentRepository).findById("1");
    }

    @Test
    void findStudentById_returnsNullIfNotFound() {
        when(studentRepository.findById("99")).thenReturn(Optional.empty());

        Student result = studentService.findStudentById("99");
        assertNull(result);

        verify(studentRepository).findById("99");
    }
}