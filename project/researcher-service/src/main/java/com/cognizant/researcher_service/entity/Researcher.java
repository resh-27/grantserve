package com.cognizant.researcher_service.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Researcher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long researcherID;

    private String name;
    private LocalDate dob;
    private String gender;
    private String institution;
    private String department;
    private String contactInfo;
    private String status;
    private Long userid;




}

