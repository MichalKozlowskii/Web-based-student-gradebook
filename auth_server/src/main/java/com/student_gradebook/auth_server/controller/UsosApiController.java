package com.student_gradebook.auth_server.controller;

import com.github.scribejava.core.model.OAuth1AccessToken;
import com.student_gradebook.auth_server.records.StudentNumberResponse;
import com.student_gradebook.auth_server.records.groups.GroupsResponse;
import com.student_gradebook.auth_server.records.TermResponse;
import com.student_gradebook.auth_server.records.groups.ParticipantRecord;
import com.student_gradebook.auth_server.service.UserService;
import com.student_gradebook.auth_server.service.UsosClient;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UsosApiController {
    private final UsosClient usosClient;
    private final UserService userService;

    @GetMapping("/fetchActiveTerm")
    @PreAuthorize("hasRole('LECTURER')")
    public TermResponse fetchActiveTerm(@AuthenticationPrincipal Jwt jwt) {
        OAuth1AccessToken accessToken = userService.fetchAccessToken(jwt);

        return usosClient.getActiveTerm(accessToken).getFirst();
    }

    @GetMapping("/fetchGroups")
    @PreAuthorize("hasRole('LECTURER')")
    public GroupsResponse fetchGroups(@AuthenticationPrincipal Jwt jwt) {
        OAuth1AccessToken accessToken = userService.fetchAccessToken(jwt);

        return usosClient.getGroups(accessToken);
    }

    @GetMapping("/fetchStudentNumber/{studentId}")
    @PreAuthorize("hasRole('LECTURER')")
    public StudentNumberResponse fetchStudentDetails(@AuthenticationPrincipal Jwt jwt,
                                                     @PathVariable("studentId") String studentId) {
        OAuth1AccessToken accessToken = userService.fetchAccessToken(jwt);

        return usosClient.getStudentNumber(accessToken, studentId);
    }
}
