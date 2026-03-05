package com.student_gradebook.attendance_service.service;

import com.student_gradebook.attendance_service.controller.exceptions.NoResponseFromApiException;
import com.student_gradebook.attendance_service.entity.Term;
import com.student_gradebook.attendance_service.repository.TermRepository;
import com.student_gradebook.attendance_service.service.client.AttendanceFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TermServiceImpl implements TermService {
    private final TermRepository termRepository;
    private final AttendanceFeignClient attendanceFeignClient;
    private Term cachedTerm;

    @Override
    public synchronized Term findActiveTerm() {
        LocalDate today = LocalDate.now();

        if (cachedTerm != null && today.isBefore(cachedTerm.getEndDate())) {
            return cachedTerm;
        }

        Optional<Term> found = termRepository.findActiveTerm(today);
        if (found.isPresent()) {
            cachedTerm = found.get();
            return cachedTerm;
        }

        Term fetched = attendanceFeignClient.fetchActiveTerm();
        if (fetched == null) {
            throw new NoResponseFromApiException("Couldn't fetch active term.");
        }

        cachedTerm = termRepository.save(fetched);

        return cachedTerm;
    }
}
