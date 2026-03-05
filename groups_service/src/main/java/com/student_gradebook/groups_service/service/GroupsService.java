package com.student_gradebook.groups_service.service;

import com.student_gradebook.groups_service.dto.GroupDetailsDto;
import com.student_gradebook.groups_service.dto.GroupPreviewDto;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupsService {
    void updateGroups(String lecturerId);
    List<GroupPreviewDto> fetchLecturerGroups(String lecturerId);
    List<GroupPreviewDto> fetchStudentGroups(String studentId);
    Optional<GroupDetailsDto> fetchGroupDetails(UUID groupId, String userId);
    Boolean canGradeByStudentId(Jwt jwt, String courseUnitId, String studentId);
    String canGradeByStudentNumber(Jwt jwt, String courseUnitId, String studentNumber);
    List<String> fetchStudentIds(Jwt jwt, String courseUnitId, Integer groupNumber);

    Boolean isAuthorisedInGroup(Jwt jwt, String courseUnitId, Integer groupNumber);
}
