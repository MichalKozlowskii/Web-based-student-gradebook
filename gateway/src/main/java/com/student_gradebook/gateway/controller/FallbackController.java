package com.student_gradebook.gateway.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class FallbackController {
    @RequestMapping("/serviceNotAvailable")
    public Mono<String> contactSupport() {
        return Mono.just("Service currently unavailable. Contact support!!");
    }
}
