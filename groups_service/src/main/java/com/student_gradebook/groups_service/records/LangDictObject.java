package com.student_gradebook.groups_service.records;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LangDictObject(
        @JsonProperty("pl") String polishText,
        @JsonProperty("en") String englishText
) {}

