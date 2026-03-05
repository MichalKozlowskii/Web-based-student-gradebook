package com.student_gradebook.groups_service.service;

import static org.junit.jupiter.api.Assertions.*;

import com.student_gradebook.groups_service.controller.exceptions.NoResponseFromApiException;
import com.student_gradebook.groups_service.entity.Term;
import com.student_gradebook.groups_service.records.TermResponse;
import com.student_gradebook.groups_service.repository.TermRepository;
import com.student_gradebook.groups_service.service.client.UsosApiFeignClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.*;
import com.student_gradebook.groups_service.records.LangDictObject;

class TermServiceImplTest {

    @Mock
    private TermRepository termRepository;

    @Mock
    private UsosApiFeignClient usosApiFeignClient;

    @InjectMocks
    private TermServiceImpl termService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void returnsCachedTermIfValid() {
        LocalDate today = LocalDate.now();
        Term repoTerm = Term.builder()
                .id("1")
                .name("Winter 2026")
                .startDate(today.minusDays(5))
                .endDate(today.plusDays(5))
                .build();

        when(termRepository.findActiveTerm(today)).thenReturn(Optional.of(repoTerm));

        // First call: fetch from repository
        Term firstCall = termService.findActiveTerm();
        assertEquals(repoTerm, firstCall);

        // Second call: should use cache, repo not called again
        Term secondCall = termService.findActiveTerm();
        assertEquals(firstCall, secondCall);

        verify(termRepository, times(1)).findActiveTerm(today);
        verifyNoInteractions(usosApiFeignClient);
    }

    @Test
    void fetchesFromRepositoryIfCachedAbsent() {
        LocalDate today = LocalDate.now();
        Term repoTerm = Term.builder()
                .id("2")
                .name("Spring 2026")
                .startDate(today.minusDays(2))
                .endDate(today.plusDays(10))
                .build();

        when(termRepository.findActiveTerm(today)).thenReturn(Optional.of(repoTerm));

        Term result = termService.findActiveTerm();

        assertEquals(repoTerm, result);

        // Second call: should use cache
        Term secondCall = termService.findActiveTerm();
        assertEquals(result, secondCall);

        verify(termRepository, times(1)).findActiveTerm(today);
        verifyNoInteractions(usosApiFeignClient);
    }

    @Test
    void fetchesFromApiIfNoTermInRepository() {
        LocalDate today = LocalDate.now();
        when(termRepository.findActiveTerm(today)).thenReturn(Optional.empty());

        LangDictObject name = new LangDictObject("Fall 2026", "Fall 2026");
        TermResponse apiResponse = new TermResponse("3", name, today, today.plusDays(15));
        when(usosApiFeignClient.fetchActiveTerm()).thenReturn(apiResponse);

        Term savedTerm = Term.builder()
                .id("3")
                .name("Fall 2026")
                .startDate(today)
                .endDate(today.plusDays(15))
                .build();
        when(termRepository.save(any(Term.class))).thenReturn(savedTerm);

        Term result = termService.findActiveTerm();

        assertEquals("3", result.getId());
        assertEquals("Fall 2026", result.getName());
        assertEquals(today, result.getStartDate());
        assertEquals(today.plusDays(15), result.getEndDate());

        // Second call: should use cachedTerm
        Term secondCall = termService.findActiveTerm();
        assertSame(result, secondCall);

        verify(termRepository).findActiveTerm(today);
        verify(usosApiFeignClient).fetchActiveTerm();
        verify(termRepository).save(any(Term.class));
    }

    @Test
    void throwsExceptionIfApiReturnsNull() {
        LocalDate today = LocalDate.now();
        when(termRepository.findActiveTerm(today)).thenReturn(Optional.empty());
        when(usosApiFeignClient.fetchActiveTerm()).thenReturn(null);

        assertThrows(NoResponseFromApiException.class, () -> termService.findActiveTerm());

        verify(termRepository).findActiveTerm(today);
        verify(usosApiFeignClient).fetchActiveTerm();
    }

    @Test
    void refreshesCacheWhenExpired() {
        LocalDate today = LocalDate.now();
        Term expiredTerm = Term.builder()
                .id("4")
                .name("Expired Term")
                .startDate(today.minusDays(10))
                .endDate(today.minusDays(1))
                .build();

        // set private cachedTerm via reflection
        try {
            var field = TermServiceImpl.class.getDeclaredField("cachedTerm");
            field.setAccessible(true);
            field.set(termService, expiredTerm);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Term repoTerm = Term.builder()
                .id("5")
                .name("New Term")
                .startDate(today)
                .endDate(today.plusDays(10))
                .build();
        when(termRepository.findActiveTerm(today)).thenReturn(Optional.of(repoTerm));

        Term result = termService.findActiveTerm();

        assertEquals("5", result.getId());
        assertEquals("New Term", result.getName());

        // next call: cached term is used
        Term secondCall = termService.findActiveTerm();
        assertSame(result, secondCall);

        verify(termRepository, times(1)).findActiveTerm(today);
        verifyNoInteractions(usosApiFeignClient);
    }
}
