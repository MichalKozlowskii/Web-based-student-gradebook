package com.student_gradebook.attendance_service.repository;

import com.student_gradebook.attendance_service.entity.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LectureRepository extends JpaRepository<Lecture, UUID> {
    List<Lecture> findAllByCourseUnitIdAndGroupNumberAndTermId(String courseUnitId, Integer groupNumber, String termId);
}
