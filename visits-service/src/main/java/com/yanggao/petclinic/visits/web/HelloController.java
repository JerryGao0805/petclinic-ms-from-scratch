package com.yanggao.petclinic.visits.web;

import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Hello", description = "Smoke / demo endpoint")
public class HelloController {

    private final String applicationName;
    private final String demoMessage;

    public HelloController(
            @Value("${spring.application.name:unknown}") String applicationName,
            @Value("${demo.message:hello}") String demoMessage) {
        this.applicationName = applicationName;
        this.demoMessage = demoMessage;
    }

    @GetMapping("/api/hello")
    public Map<String, Object> hello() {
        return Map.of(
                "service", applicationName,
                "message", demoMessage,
                "timestamp", Instant.now().toString()
        );
    }
}
