package com.student_gradebook.attendance_service.repository;

import com.student_gradebook.attendance_service.entity.Term;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;

public interface TermRepository extends JpaRepository<Term, String> {
    @Query("SELECT t FROM Term t WHERE t.startDate <= :date AND t.endDate >= :date")
    Optional<Term> findActiveTerm(LocalDate date);
}
