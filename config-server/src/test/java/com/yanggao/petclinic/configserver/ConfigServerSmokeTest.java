package com.yanggao.petclinic.configserver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ConfigServerSmokeTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void healthEndpointUp() {
        ResponseEntity<String> resp = restTemplate.getForEntity("/actuator/health", String.class);
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(resp.getBody()).contains("\"status\":\"UP\"");
    }

    @Test
    void servesApplicationConfig() {
        ResponseEntity<String> resp = restTemplate.getForEntity("/application/default", String.class);
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(resp.getBody()).contains("hello-from-config-server");
    }
}
