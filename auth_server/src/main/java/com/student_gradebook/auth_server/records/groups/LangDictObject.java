package com.student_gradebook.auth_server.records.groups;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LangDictObject(
        @JsonProperty("pl") String polishText,
        @JsonProperty("en") String englishText
) {}
