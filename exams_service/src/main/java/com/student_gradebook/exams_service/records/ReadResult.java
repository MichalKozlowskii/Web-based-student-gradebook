package com.student_gradebook.exams_service.records;

import java.util.List;

public record ReadResult(
        List<Line> lines
) {
}