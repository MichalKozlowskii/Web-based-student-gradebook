package com.student_gradebook.groups_service.service;

import com.student_gradebook.groups_service.controller.exceptions.NoResponseFromApiException;
import com.student_gradebook.groups_service.entity.Term;
import com.student_gradebook.groups_service.records.TermResponse;
import com.student_gradebook.groups_service.repository.TermRepository;
import com.student_gradebook.groups_service.service.client.UsosApiFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TermServiceImpl implements TermService {
    private final TermRepository termRepository;
    private final UsosApiFeignClient usosApiFeignClient;
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

        TermResponse fetched = usosApiFeignClient.fetchActiveTerm();
        if (fetched == null) {
            throw new NoResponseFromApiException("Couldn't fetch active term from Usos API.");
        }

        Term newTerm = Term.builder()
                .id(fetched.id())
                .name(fetched.name().polishText())
                .startDate(fetched.startDate())
                .endDate(fetched.endDate())
                .build();
        cachedTerm = termRepository.save(newTerm);

        return cachedTerm;
    }
}
