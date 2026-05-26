package com.cts.grantserve.Application_Service.entity;

import com.cts.grantserve.Application_Service.enums.ApplicationStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class GrantApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationID;
    private Long researcherId;
    private Long programId;
    private String title;
    private LocalDate submittedDate;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    @OneToMany(mappedBy = "grantApplication", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Proposal> proposals= new ArrayList<>();
}