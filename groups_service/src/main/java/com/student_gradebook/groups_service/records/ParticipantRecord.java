package com.student_gradebook.groups_service.records;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ParticipantRecord(
        @JsonProperty("id") String id,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName,
        @JsonProperty("student_number") String studentNumber
) {}
