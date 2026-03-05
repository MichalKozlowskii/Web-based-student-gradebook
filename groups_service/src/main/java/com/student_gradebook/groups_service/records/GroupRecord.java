package com.student_gradebook.groups_service.records;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record GroupRecord(
        @JsonProperty("course_unit_id") String courseUnitId,
        @JsonProperty("group_number") Integer groupNumber,
        @JsonProperty("course_id") String courseId,
        @JsonProperty("course_name") LangDictObject courseName,
        @JsonProperty("participants") List<ParticipantRecord> participants
) {}
