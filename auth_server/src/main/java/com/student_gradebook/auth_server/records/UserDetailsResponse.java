package com.student_gradebook.auth_server.records;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserDetailsResponse(
        @JsonProperty("id") String id,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName,
        @JsonProperty("student_status") Integer studentStatus,
        @JsonProperty("staff_status") Integer staffStatus
) {}
