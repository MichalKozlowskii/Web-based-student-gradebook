package com.student_gradebook.groups_service.service.client;

import com.student_gradebook.groups_service.config.FeignJwtConfig;
import com.student_gradebook.groups_service.records.GroupsResponse;
import com.student_gradebook.groups_service.records.ParticipantRecord;
import com.student_gradebook.groups_service.records.TermResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "cards",
        url = "http://auth:8081",
        configuration = FeignJwtConfig.class,
        fallback = UsosApiFallback.class
)
@Primary
public interface UsosApiFeignClient {
    @GetMapping(value ="/fetchActiveTerm", consumes = "application/json")
    TermResponse fetchActiveTerm();

    @GetMapping(value = "/fetchGroups", consumes = "application/json")
    GroupsResponse fetchGroups();

    @GetMapping(value = "/fetchStudentDetails/{studentId}", consumes = "application/json")
    ParticipantRecord fetchStudentDetails(@PathVariable("studentId") String studentId);
}
