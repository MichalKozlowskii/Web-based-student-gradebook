package com.student_gradebook.auth_server.records;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.student_gradebook.auth_server.records.groups.LangDictObject;

import java.time.LocalDate;

public record TermResponse(
        @JsonProperty("id") String id,
        @JsonProperty("name") LangDictObject name,
        @JsonProperty("start_date") LocalDate startDate,
        @JsonProperty("finish_date") LocalDate endDate
) {}
