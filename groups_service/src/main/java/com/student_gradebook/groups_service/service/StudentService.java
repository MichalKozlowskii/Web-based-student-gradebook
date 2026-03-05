package com.student_gradebook.groups_service.service;

import com.student_gradebook.groups_service.entity.Student;
import com.student_gradebook.groups_service.records.ParticipantRecord;

import java.util.List;

public interface StudentService {
    List<Student> findOrSaveParticipants(List<ParticipantRecord> participants);
    Student findStudentById(String id);
    Student findByStudentNumber(String studentNumber);
}
