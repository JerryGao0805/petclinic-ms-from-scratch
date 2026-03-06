package com.yanggao.petclinic.visits.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yanggao.petclinic.visits.model.Visit;

public interface VisitRepository extends JpaRepository<Visit, Long> {
    List<Visit> findByPetId(Long petId);

    List<Visit> findByPetIdOrderByVisitDateDesc(Long petId);
}
