package com.student_gradebook.summary_service.service.client;

import com.student_gradebook.summary_service.config.FeignJwtConfig;
import com.student_gradebook.summary_service.records.GradeRecord;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@FeignClient(
        name = "grades",
        url = "http://grades:9000",
        configuration = FeignJwtConfig.class,
        fallback = GradesFeignFallback.class
)
@Primary
public interface GradesFeignClient {
    @GetMapping(value = "/api/fetch/course/{courseUnitId}", consumes = "application/json")
    List<GradeRecord> fetchStudentsGrades(@PathVariable("courseUnitId") String courseUnitId);

    @GetMapping(value = "/api/fetch/course/{courseUnitId}/{groupNumber}", consumes = "application/json")
    Map<String, List<GradeRecord>> fetchGradesInGroup(@PathVariable("courseUnitId") String courseUnitId,
                                                      @PathVariable("groupNumber") Integer groupNumber);
}
