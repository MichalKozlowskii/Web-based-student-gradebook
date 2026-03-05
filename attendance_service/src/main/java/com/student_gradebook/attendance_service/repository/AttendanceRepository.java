package com.student_gradebook.attendance_service.repository;

import com.student_gradebook.attendance_service.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AttendanceRepository extends JpaRepository<Attendance, UUID> {
    List<Attendance> findAllByStudentIdAndLecture_CourseUnitIdAndLecture_TermId(
            String studentId,
            String courseUnitId,
            String termId
    );
}
