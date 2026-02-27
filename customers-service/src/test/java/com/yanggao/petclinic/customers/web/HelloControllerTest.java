package com.yanggao.petclinic.customers.web;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.cloud.config.enabled=false",
                "spring.cloud.config.import-check.enabled=false",
                "eureka.client.enabled=false",
                "spring.cloud.discovery.enabled=false",
                "demo.message=test-from-test"
        }
)
class HelloControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void hello_returnsServiceAndMessage() {
        ResponseEntity<Map> response = restTemplate.getForEntity("/api/hello", Map.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("service")).isEqualTo("customers-service");
        assertThat(response.getBody().get("message")).isEqualTo("test-from-test");
        assertThat(response.getBody().get("timestamp")).isNotNull();
    }
}
