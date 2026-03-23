package com.student_gradebook.groups_service.service;

import com.student_gradebook.groups_service.entity.Student;
import com.student_gradebook.groups_service.records.ParticipantRecord;
import com.student_gradebook.groups_service.repository.StudentRepository;
import com.student_gradebook.groups_service.service.client.UsosApiFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final UsosApiFeignClient usosApiFeignClient;

    @Override
    public List<Student> findOrSaveParticipants(List<ParticipantRecord> participants) {
        List<Student> students = new ArrayList<>();

        for (ParticipantRecord record : participants) {
            Optional<Student> studentOptional = studentRepository.findById(record.id());

            if (studentOptional.isPresent()) {
                Student student = studentOptional.get();
                if (student.getStudentNumber() == null) {
                    String studentNumber = usosApiFeignClient.fetchStudentNumber(student.getId()).studentNumber();
                    student.setStudentNumber(studentNumber);
                    student = studentRepository.save(student);
                }
                students.add(student);
                continue;
            }

            String studentNumber = usosApiFeignClient.fetchStudentNumber(record.id()).studentNumber();

            Student savedStudent = studentRepository.save(Student.builder()
                    .id(record.id())
                    .firstName(record.firstName())
                    .lastName(record.lastName().substring(0, 3))
                    .studentNumber(studentNumber)
                    .build());

            students.add(savedStudent);
        }

        return students;
    }

    @Override
    public Student findStudentById(String id) {
        return studentRepository.findById(id).orElse(null);
    }

    @Override
    public Student findByStudentNumber(String studentNumber) {
        return studentRepository.findByStudentNumber(studentNumber).orElse(null);
    }
}
