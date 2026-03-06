package com.yanggao.petclinic.visits.web.dto;

import java.time.LocalDate;
import com.yanggao.petclinic.visits.model.Visit;

public record VisitResponse(Long id, Long petId, LocalDate visitDate, String description) {
    public static VisitResponse fromEntity(Visit visit) {
        return new VisitResponse(
                visit.getId(),
                visit.getPetId(),
                visit.getVisitDate(),
                visit.getDescription()
        );
    }
}
