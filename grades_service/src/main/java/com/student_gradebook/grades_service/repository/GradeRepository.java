package com.student_gradebook.grades_service.repository;

import com.student_gradebook.grades_service.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GradeRepository extends JpaRepository<Grade, UUID> {
    List<Grade> findAllByCourseUnitIdAndStudentIdAndTermId(String courseUnitId, String studentId, String termId);
    List<Grade> findTop5ByStudentIdAndTermIdOrderByLastUpdatedDesc(String studentId, String termId);
}
