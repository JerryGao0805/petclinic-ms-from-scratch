package com.yanggao.petclinic.visits.web;

import java.time.LocalDate;

import com.yanggao.petclinic.visits.model.Visit;
import com.yanggao.petclinic.visits.service.VisitNotFoundException;
import com.yanggao.petclinic.visits.service.VisitService;
import com.yanggao.petclinic.visits.web.error.ApiExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = VisitController.class,
        properties = {
                "spring.cloud.config.enabled=false",
                "spring.cloud.config.import-check.enabled=false",
                "eureka.client.enabled=false",
                "spring.cloud.discovery.enabled=false"
        }
)
@Import(ApiExceptionHandler.class)
class VisitControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VisitService visitService;

    @Test
    void create_validRequest_returns201AndBody() throws Exception {
        Visit saved = new Visit(10L, LocalDate.of(2026, 3, 6), "from test");
        ReflectionTestUtils.setField(saved, "id", 123L);

        given(visitService.create(any())).willReturn(saved);

        mockMvc.perform(post("/api/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                  {"petId":10,"visitDate":"2026-03-06","description":"from test"}
                                  """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(123))
                .andExpect(jsonPath("$.petId").value(10))
                .andExpect(jsonPath("$.visitDate").value("2026-03-06"))
                .andExpect(jsonPath("$.description").value("from test"));
    }

    @Test
    void create_missingVisitDate_returns400ProblemDetailWithErrors() throws Exception {
        mockMvc.perform(post("/api/visits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                  {"petId":1,"description":"missing date"}
                                  """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.type").value("urn:problem-type:validation-error"))
                .andExpect(jsonPath("$.errors[0].field").value("visitDate"));
    }

    @Test
    void getById_notFound_returns404ProblemDetail() throws Exception {
        willThrow(new VisitNotFoundException(999999L)).given(visitService).getById(999999L);

        mockMvc.perform(get("/api/visits/999999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.type").value("urn:problem-type:not-found"))
                .andExpect(jsonPath("$.id").value(999999));
    }
}
