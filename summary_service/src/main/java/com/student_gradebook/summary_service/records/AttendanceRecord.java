package com.student_gradebook.summary_service.records;

import com.student_gradebook.summary_service.enums.Status;

public record AttendanceRecord(
        Status status
) {}
