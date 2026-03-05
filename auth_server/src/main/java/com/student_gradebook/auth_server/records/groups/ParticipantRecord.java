package com.student_gradebook.auth_server.records.groups;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ParticipantRecord(
        @JsonProperty("id") String id,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName,
        @JsonProperty("student_number") String studentNumber
) {}
