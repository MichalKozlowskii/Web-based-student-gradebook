package com.student_gradebook.groups_service.records;

import java.util.List;
import java.util.Map;

public record GroupsResponse(
        Map<String, List<GroupRecord>> groups
) {}
