package com.student_gradebook.groups_service.controller;

import com.student_gradebook.groups_service.entity.Term;
import com.student_gradebook.groups_service.service.TermService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TermController {
    private final TermService termService;

    @GetMapping("/activeTerm")
    public ResponseEntity<Term> fetchActiveTerm() {
        return ResponseEntity.ok().body(termService.findActiveTerm());
    }
}
