package com.cts.grantserve.Application_Service.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="proposal")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Proposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long proposalID;

    private String fileURI;
    private LocalDateTime submittedDate;
    private String status;

    @ManyToOne
    @JoinColumn(name = "applicationID")
    @JsonBackReference
    private GrantApplication grantApplication;






}