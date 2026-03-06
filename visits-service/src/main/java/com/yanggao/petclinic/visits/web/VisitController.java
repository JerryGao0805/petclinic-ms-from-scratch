package com.yanggao.petclinic.visits.web;
import java.util.List;

import com.yanggao.petclinic.visits.model.Visit;
import com.yanggao.petclinic.visits.service.VisitService;
import com.yanggao.petclinic.visits.web.dto.VisitCreateRequest;
import com.yanggao.petclinic.visits.web.dto.VisitResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/visits")
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
}
