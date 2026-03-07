package com.yanggao.petclinic.visits.service;

import java.util.List;

import com.yanggao.petclinic.visits.web.dto.VisitUpdateRequest;
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

    @Transactional(readOnly = true)
    public Visit getById(Long id) {
        return visitRepository.findById(id)
                .orElseThrow(() -> new VisitNotFoundException(id));
    }

    @Transactional
    public void deleteById(Long id) {
        Visit visit = visitRepository.findById(id)
                .orElseThrow(() -> new VisitNotFoundException(id));
        visitRepository.delete(visit);
    }

    @Transactional
    public Visit update(Long id, VisitUpdateRequest request) {
        Visit visit = getById(id);
        visit.update(request.petId(), request.visitDate(), request.description());
        return visit;
    }

}
