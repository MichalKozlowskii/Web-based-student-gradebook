package com.student_gradebook.groups_service.repository;

import com.student_gradebook.groups_service.entity.Group;
import com.student_gradebook.groups_service.entity.Student;
import com.student_gradebook.groups_service.entity.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupRepository extends JpaRepository<Group, UUID> {
    Optional<Group> findByCourseUnitIdAndGroupNumberAndTermAndLecturerId(String courseUnitId, Integer groupNumber,
                                                                         Term term, String lecturerId);
    List<Group> findAllByLecturerIdAndTermId(String lecturerId, String termId);
    List<Group> findAllByParticipantsIdAndTermId(String studentId, String termId);
    @Query("""
    SELECT g FROM Group g
    LEFT JOIN g.participants p
    WHERE g.id = :groupId
      AND (g.lecturerId = :userId OR p.id = :userId)
    """)
    Optional<Group> findAccessibleGroupById(
            @Param("groupId") UUID groupId,
            @Param("userId") String userId
    );

    Boolean existsByCourseUnitIdAndLecturerIdAndTermAndParticipantsContaining(String courseUnitId, String lecturerId,
                                                                              Term term, Student participant);

    @Query("""
    SELECT p.id
    FROM Group g
    JOIN g.participants p
    WHERE g.courseUnitId = :courseUnitId
      AND g.groupNumber = :groupNumber
      AND g.lecturerId = :lecturerId
      AND g.term = :term
    """)
    List<String> findParticipantIds(
            String courseUnitId,
            Integer groupNumber,
            String lecturerId,
            Term term
    );

    Boolean existsByCourseUnitIdAndGroupNumberAndLecturerIdAndTerm(String courseUnitId, Integer groupNumber,
                                                                   String lecturerId, Term term);
}
