package com.yanggao.petclinic.visits.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI visitsServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Petclinic Visits Service API")
                        .description("CRUD API for visits. Error responses follow RFC7807 (application/problem+json).")
                        .version("v1"))
                .addServersItem(new Server()
                        .url("http://localhost:8083")
                        .description("Direct (dev)"))
                .addServersItem(new Server()
                        .url("http://localhost:8080/visits")
                        .description("Via API Gateway (dev)"));
    }
}
