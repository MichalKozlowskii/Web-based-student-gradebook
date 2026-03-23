package com.student_gradebook.groups_service.service.client;

import com.student_gradebook.groups_service.records.GroupsResponse;
import com.student_gradebook.groups_service.records.ParticipantRecord;
import com.student_gradebook.groups_service.records.StudentNumberResponse;
import com.student_gradebook.groups_service.records.TermResponse;
import org.springframework.stereotype.Component;

@Component
public class UsosApiFallback implements UsosApiFeignClient {
    @Override
    public TermResponse fetchActiveTerm() {
        return null;
    }

    @Override
    public GroupsResponse fetchGroups() {
        return null;
    }

    @Override
    public StudentNumberResponse fetchStudentNumber(String studentId) {
        return null;
    }
}
