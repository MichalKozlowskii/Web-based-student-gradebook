package com.student_gradebook.summary_service.service.client;

import com.student_gradebook.summary_service.config.FeignJwtConfig;
import com.student_gradebook.summary_service.records.AttendanceRecord;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@FeignClient(
        name = "attendance",
        url = "http://attendance:9010",
        configuration = FeignJwtConfig.class,
        fallback = AttendanceFeignFallback.class
)
@Primary
public interface AttendanceFeignClient {
    @GetMapping(value ="/api/fetch/{courseUnitId}", consumes = "application/json")
    List<AttendanceRecord>  fetchStudentsAttendance(@PathVariable("courseUnitId") String courseUnitId);

    @GetMapping(value = "/api/fetch/{courseUnitId}/{groupNumber}", consumes = "application/json")
    Map<String, List<AttendanceRecord>> fetchAttendanceInGroup(@PathVariable("courseUnitId") String courseUnitId,
                                                               @PathVariable("groupNumber") Integer groupNumber);
}
