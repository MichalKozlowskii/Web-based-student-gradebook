package com.student_gradebook.groups_service.records;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record TermResponse(
        @JsonProperty("id") String id,
        @JsonProperty("name") LangDictObject name,
        @JsonProperty("start_date") LocalDate startDate,
        @JsonProperty("finish_date") LocalDate endDate
) {}
