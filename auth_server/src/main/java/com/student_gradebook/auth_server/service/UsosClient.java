package com.student_gradebook.auth_server.service;

import com.github.scribejava.core.model.OAuth1AccessToken;
import com.student_gradebook.auth_server.records.StudentNumberResponse;
import com.student_gradebook.auth_server.records.groups.GroupsResponse;
import com.student_gradebook.auth_server.records.TermResponse;
import com.student_gradebook.auth_server.records.UserDetailsResponse;
import com.student_gradebook.auth_server.records.groups.ParticipantRecord;

import java.util.List;

public interface UsosClient {
    UserDetailsResponse getUserDetails(OAuth1AccessToken accessToken);
    List<TermResponse> getActiveTerm(OAuth1AccessToken accessToken);
    GroupsResponse getGroups(OAuth1AccessToken accessToken);
    StudentNumberResponse getStudentNumber(OAuth1AccessToken accessToken, String userId);
}