package com.yanggao.petclinic.vets.web;

import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
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
