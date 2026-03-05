package com.student_gradebook.groups_service.controller;

import com.student_gradebook.groups_service.dto.GroupDetailsDto;
import com.student_gradebook.groups_service.dto.GroupPreviewDto;
import com.student_gradebook.groups_service.service.GroupsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GroupsController {
    private final GroupsService groupsService;

    @PostMapping("/update")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<String> updateGroups(@AuthenticationPrincipal Jwt jwt) {
        String lecturerId = jwt.getClaimAsString("id");
        groupsService.updateGroups(lecturerId);

        return ResponseEntity.ok()
                .body("Groups updated!");
    }

    @GetMapping("/fetch")
    public ResponseEntity<List<GroupPreviewDto>> fetchGroups(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getClaimAsString("id");
        String role = jwt.getClaimAsString("role");

        List<GroupPreviewDto> groups;

        if (Objects.equals(role, "LECTURER")) {
            groups = groupsService.fetchLecturerGroups(userId);
        } else {
            groups = groupsService.fetchStudentGroups(userId);
        }

        return ResponseEntity.ok()
                .body(groups);
    }

    @GetMapping("/fetch/{groupId}")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<GroupDetailsDto> fetchGroupDetails(@AuthenticationPrincipal Jwt jwt,
                                                             @PathVariable("groupId") UUID groupId) {
        Optional<GroupDetailsDto> details =
                groupsService.fetchGroupDetails(groupId, jwt.getClaimAsString("id"));

        return details
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }

    // == endpoints only used by other microservices ==
    @GetMapping("/canGradeByStudentNumber/{courseUnitId}/{studentNumber}")
    @PreAuthorize("hasRole('LECTURER')")
    public String canGradeByStudentNumber(@AuthenticationPrincipal Jwt jwt,
                                           @PathVariable("courseUnitId") String courseUnitId,
                                           @PathVariable("studentNumber") String studentNumber) {
        return groupsService.canGradeByStudentNumber(jwt, courseUnitId, studentNumber);
    }

    @GetMapping("/canGrade/{courseUnitId}/{studentId}")
    @PreAuthorize("hasRole('LECTURER')")
    public Boolean canGradeByStudentId(@AuthenticationPrincipal Jwt jwt,
                                       @PathVariable("courseUnitId") String courseUnitId,
                                       @PathVariable("studentId") String studentId) {
        return groupsService.canGradeByStudentId(jwt, courseUnitId, studentId);
    }

    @GetMapping("/fetchStudentIds/{courseUnitId}/{groupNumber}")
    @PreAuthorize("hasRole('LECTURER')")
    public List<String> fetchStudentIds(@AuthenticationPrincipal Jwt jwt,
                                        @PathVariable("courseUnitId") String courseUnitId,
                                        @PathVariable("groupNumber") Integer groupNumber) {
        return groupsService.fetchStudentIds(jwt, courseUnitId, groupNumber);
    }

    @GetMapping("/isAuthorised/{courseUnitId}/{groupNumber}")
    @PreAuthorize("hasRole('LECTURER')")
    public Boolean isAuthorisedInGroup(@AuthenticationPrincipal Jwt jwt,
                                       @PathVariable("courseUnitId") String courseUnitId,
                                       @PathVariable("groupNumber") Integer groupNumber) {
        return groupsService.isAuthorisedInGroup(jwt, courseUnitId, groupNumber);
    }
}
