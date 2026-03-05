package com.student_gradebook.attendance_service.service.client;

import com.student_gradebook.attendance_service.config.FeignJwtConfig;
import com.student_gradebook.attendance_service.entity.Term;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
        name = "attendance",
        url = "http://groups:8090",
        configuration = FeignJwtConfig.class,
        fallback = AttendanceFeignFallback.class
)
@Primary
public interface AttendanceFeignClient {
    @GetMapping(value = "/api/activeTerm", consumes = "application/json")
    Term fetchActiveTerm();

    @GetMapping(value = "/api/isAuthorised/{courseUnitId}/{groupNumber}", consumes = "application/json")
    Boolean isAuthorisedInGroup(@PathVariable("courseUnitId") String courseUnitId,
                                @PathVariable("groupNumber") Integer groupNumber);
    @GetMapping(value = "/api/fetchStudentIds/{courseUnitId}/{groupNumber}", consumes = "application/json")
    List<String> fetchStudentIds(@PathVariable("courseUnitId") String courseUnitId,
                                 @PathVariable("groupNumber") Integer groupNumber);
}