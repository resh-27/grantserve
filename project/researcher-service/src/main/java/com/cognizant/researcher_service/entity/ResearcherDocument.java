package com.cognizant.researcher_service.entity;

import com.cognizant.researcher_service.enums.DocType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResearcherDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long documentID;

    // Change from researcher_id to researcherID
    private Long researcherID;

    @Enumerated(EnumType.STRING)
    private DocType docType;

    private String fileURI;
    private LocalDateTime uploadedDate;
    private String verificationStatus;
}