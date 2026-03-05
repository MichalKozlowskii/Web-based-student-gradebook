package com.student_gradebook.exams_service.repository;

import com.student_gradebook.exams_service.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ExamRepository extends JpaRepository<Exam, UUID> {
    List<Exam> findAllByLecturerIdOrderByLastUpdatedDesc(String lecturerId);
}
