package com.student_gradebook.auth_server.records;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StudentNumberResponse(
        @JsonProperty("student_number") String studentNumber
)
{}
