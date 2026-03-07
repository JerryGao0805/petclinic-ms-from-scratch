package com.yanggao.petclinic.visits.service;

public class VisitNotFoundException extends RuntimeException {
    private final Long id;

    public VisitNotFoundException(Long id) {
        super("Visit not found: " + id);
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}

