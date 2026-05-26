package com.cognizant.review_service.entity;

import com.cognizant.review_service.enums.ReviewStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewID;

    private Long proposalID;

    private Long  reviewerID;

    private Integer score;
    private String comments;
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private ReviewStatus status;
}