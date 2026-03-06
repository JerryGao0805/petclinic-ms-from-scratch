package com.yanggao.petclinic.visits.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "visits")
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pet_id", nullable = false)
    private Long petId;

    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate;

    @Column(nullable = false, length = 255)
    private String description;

    protected Visit() {
    }

    public Visit(Long petId, LocalDate visitDate, String description) {
        this.petId = petId;
        this.visitDate = visitDate;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public Long getPetId() {
        return petId;
    }

    public LocalDate getVisitDate() {
        return visitDate;
    }

    public String getDescription() {
        return description;
    }

//    public void setPetId(Long petId) {
//        this.petId = petId;
//    }
//
//    public void setVisitDate(LocalDate visitDate) {
//        this.visitDate = visitDate;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
}
