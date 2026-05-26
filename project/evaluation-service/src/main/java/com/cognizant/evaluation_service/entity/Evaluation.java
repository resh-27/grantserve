package com.cognizant.evaluation_service.entity;

import com.cognizant.evaluation_service.enums.Result;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Evaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long evaluationID;

    private Long  applicationID;

    @Enumerated(EnumType.STRING)
    private Result result;

    private LocalDate date;
    private String notes;
}
