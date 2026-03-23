package com.student_gradebook.groups_service.records;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StudentNumberResponse(
        @JsonProperty("student_number") String studentNumber
)
{}