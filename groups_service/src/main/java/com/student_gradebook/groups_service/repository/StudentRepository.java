package com.student_gradebook.groups_service.repository;

import com.student_gradebook.groups_service.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, String> {
    Optional<Student> findByStudentNumber(String studentNumber);
}
