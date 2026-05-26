package com.cts.grantserve.program_service.entity;

import com.cts.grantserve.program_service.enums.ProgramStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Program {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "program_id")
    private Long programID;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDate startDate;
    private LocalDate endDate;
    private Double budget;

    @Enumerated(EnumType.STRING)
    private ProgramStatus status;

//    @OneToOne(mappedBy = "program", cascade = CascadeType.ALL)
//    @JsonManagedReference
    @Column(name = "budget_id")
    private Long budgetId;

}