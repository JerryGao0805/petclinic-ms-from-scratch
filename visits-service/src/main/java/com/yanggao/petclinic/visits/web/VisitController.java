package com.yanggao.petclinic.visits.web;
import java.util.List;

import com.yanggao.petclinic.visits.model.Visit;
import com.yanggao.petclinic.visits.service.VisitService;
import com.yanggao.petclinic.visits.web.dto.VisitCreateRequest;
import com.yanggao.petclinic.visits.web.dto.VisitResponse;
import com.yanggao.petclinic.visits.web.dto.VisitUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/visits")
@Tag(name = "Visits", description = "Visits CRUD API")
public class VisitController {

    private final VisitService visitService;

    public VisitController(VisitService visitService) {
        this.visitService = visitService;
    }

    @GetMapping
    public List<VisitResponse> listByPetId(@RequestParam Long petId) {
        return visitService.listByPetId(petId)
                .stream()
                .map(VisitResponse::fromEntity)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VisitResponse create(@Valid @RequestBody VisitCreateRequest request) {
        return VisitResponse.fromEntity(visitService.create(request));
    }

    @GetMapping("/{id}")
    public VisitResponse getById(@PathVariable Long id) {
        return VisitResponse.fromEntity(visitService.getById(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        visitService.deleteById(id);
    }

    @PutMapping("/{id}")
    public VisitResponse update(@PathVariable Long id, @Valid @RequestBody VisitUpdateRequest request) {
        return VisitResponse.fromEntity(visitService.update(id, request));
    }
}
