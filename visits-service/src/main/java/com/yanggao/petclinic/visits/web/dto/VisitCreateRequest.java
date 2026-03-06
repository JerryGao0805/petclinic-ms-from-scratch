package com.yanggao.petclinic.visits.web.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VisitCreateRequest(
    @NotNull Long petId,
    @NotNull LocalDate visitDate,
    @NotBlank @Size(max = 255) String description
) {}
