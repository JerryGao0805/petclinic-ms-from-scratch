package com.yanggao.petclinic.visits.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.yanggao.petclinic.visits.model.Visit;
import com.yanggao.petclinic.visits.repo.VisitRepository;
import com.yanggao.petclinic.visits.web.dto.VisitCreateRequest;

@Service
public class VisitService {
    private final VisitRepository visitRepository;

    public VisitService(VisitRepository visitRepository) {
        this.visitRepository = visitRepository;
    }

    @Transactional(readOnly = true)
    public List<Visit> listByPetId(Long petId) {
        return visitRepository.findByPetIdOrderByVisitDateDesc(petId);
    }

    @Transactional
    public Visit create(VisitCreateRequest request) {
        Visit visit = new Visit(request.petId(), request.visitDate(), request.description());
        return visitRepository.save(visit);
    }
}
