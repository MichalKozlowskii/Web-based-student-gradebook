package com.student_gradebook.grades_service.service.client;

import com.student_gradebook.grades_service.config.FeignJwtConfig;
import com.student_gradebook.grades_service.entity.Term;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
        name = "grades",
        url = "http://groups:8090",
        configuration = FeignJwtConfig.class,
        fallback = GradesFeignFallback.class
)
@Primary
public interface GradesFeignClient {
    @GetMapping(value = "/api/activeTerm", consumes = "application/json")
    Term fetchActiveTerm();

    @GetMapping(value = "api/canGrade/{courseUnitId}/{studentId}", consumes = "application/json")
    Boolean canGrade(@PathVariable("courseUnitId") String courseUnitId,
                     @PathVariable("studentId") String studentId);

    @GetMapping(value = "/api/fetchStudentIds/{courseUnitId}/{groupNumber}", consumes = "application/json")
    List<String> fetchStudentIds(@PathVariable("courseUnitId") String courseUnitId,
                                 @PathVariable("groupNumber") Integer groupNumber);

    @GetMapping(value = "/api/canGradeByStudentNumber/{courseUnitId}/{studentNumber}", consumes = "application/json")
    String canGradeByStudentNumber(@PathVariable("courseUnitId") String courseUnitId,
                                   @PathVariable("studentNumber") String studentNumber);
}