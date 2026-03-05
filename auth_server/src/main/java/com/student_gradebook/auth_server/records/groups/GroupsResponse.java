package com.student_gradebook.auth_server.records.groups;

import java.util.List;
import java.util.Map;

public record GroupsResponse(
   Map<String, List<GroupRecord>> groups
) {}
