package com.student_gradebook.groups_service.service;

import com.student_gradebook.groups_service.controller.exceptions.NoResponseFromApiException;
import com.student_gradebook.groups_service.dto.GroupDetailsDto;
import com.student_gradebook.groups_service.dto.GroupPreviewDto;
import com.student_gradebook.groups_service.entity.Group;
import com.student_gradebook.groups_service.entity.Student;
import com.student_gradebook.groups_service.entity.Term;
import com.student_gradebook.groups_service.mappers.GroupMapper;
import com.student_gradebook.groups_service.records.GroupRecord;
import com.student_gradebook.groups_service.records.GroupsResponse;
import com.student_gradebook.groups_service.repository.GroupRepository;
import com.student_gradebook.groups_service.service.client.UsosApiFeignClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupsServiceImpl implements GroupsService {
    private final GroupRepository groupRepository;
    private final StudentService studentService;
    private final TermService termService;
    private final UsosApiFeignClient feignClient;

    private GroupsResponse fetchGroupsFromUsos() {
        GroupsResponse response = feignClient.fetchGroups();
        if (response == null) {
            throw new NoResponseFromApiException("Couldn't fetch groups from Usos API.");
        }

        return response;
    }

    @Override
    @Transactional
    public void updateGroups(String lecturerId) {
        Term term = termService.findActiveTerm();
        GroupsResponse groupsResponse = fetchGroupsFromUsos();

        List<GroupRecord> groupRecords = groupsResponse.groups().get(term.getId());
        if (groupRecords.isEmpty()) throw new NoResponseFromApiException("You don't have any groups.");

        for (GroupRecord record : groupRecords) {
            List<Student> participants = studentService.findOrSaveParticipants(record.participants());

            Optional<Group> optionalGroup = groupRepository.findByCourseUnitIdAndGroupNumberAndTermAndLecturerId(
                    record.courseUnitId(), record.groupNumber(), term, lecturerId);

            if (optionalGroup.isEmpty()) {
                groupRepository.save(Group.builder()
                                .groupNumber(record.groupNumber())
                                .courseName(record.courseName().polishText())
                                .courseUnitId(record.courseUnitId())
                                .participants(participants)
                                .term(term)
                                .lecturerId(lecturerId)
                        .build());
            } else {
                Group foundGroup = optionalGroup.get();

                boolean needsUpdate = !new HashSet<>(participants)
                        .equals(new HashSet<>(foundGroup.getParticipants()));

                if (needsUpdate) {
                    foundGroup.setParticipants(participants);
                    groupRepository.save(foundGroup);
                }
            }
        }
    }

    @Override
    public List<GroupPreviewDto> fetchLecturerGroups(String lecturerId) {
        Term term = termService.findActiveTerm();

        return groupRepository.findAllByLecturerIdAndTermId(lecturerId, term.getId())
                .stream()
                .map(GroupMapper::mapToGroupPreviewDto)
                .toList();
    }

    @Override
    public List<GroupPreviewDto> fetchStudentGroups(String studentId) {
        Term term = termService.findActiveTerm();

        return groupRepository.findAllByParticipantsIdAndTermId(studentId, term.getId())
                .stream()
                .map(GroupMapper::mapToGroupPreviewDto)
                .toList();
    }

    @Override
    public Optional<GroupDetailsDto> fetchGroupDetails(UUID groupId, String userId) {
        Optional<Group> optionalGroup = groupRepository.findAccessibleGroupById(groupId, userId);

        return optionalGroup.map(GroupMapper::mapToGroupDetailsDto);
    }

    @Override
    public Boolean canGradeByStudentId(Jwt jwt, String courseUnitId, String studentId) {
        Student gradedStudent = studentService.findStudentById(studentId);
        if (gradedStudent == null) return false;

        String lecturerId = jwt.getClaimAsString("id");
        Term activeTerm = termService.findActiveTerm();

        return groupRepository.existsByCourseUnitIdAndLecturerIdAndTermAndParticipantsContaining(
                courseUnitId, lecturerId, activeTerm, gradedStudent);
    }

    @Override
    public String canGradeByStudentNumber(Jwt jwt, String courseUnitId, String studentNumber) {
        Student gradedStudent = studentService.findByStudentNumber(studentNumber);
        if (gradedStudent == null) return null;

        String lecturerId = jwt.getClaimAsString("id");
        Term activeTerm = termService.findActiveTerm();

        Boolean canGrade = groupRepository.existsByCourseUnitIdAndLecturerIdAndTermAndParticipantsContaining(
                courseUnitId, lecturerId, activeTerm, gradedStudent);

        return canGrade ? gradedStudent.getId() : null;
    }

    @Override
    public List<String> fetchStudentIds(Jwt jwt, String courseUnitId, Integer groupNumber) {
        String lecturerId = jwt.getClaimAsString("id");

        return groupRepository.findParticipantIds(courseUnitId, groupNumber, lecturerId, termService.findActiveTerm());
    }

    @Override
    public Boolean isAuthorisedInGroup(Jwt jwt , String courseUnitId, Integer groupNumber) {
        String lecturerId = jwt.getClaimAsString("id");

        return groupRepository.existsByCourseUnitIdAndGroupNumberAndLecturerIdAndTerm(courseUnitId, groupNumber,
                lecturerId, termService.findActiveTerm());
    }
}
