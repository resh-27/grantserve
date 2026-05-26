package com.cognizant.review_service.repository;

import com.cognizant.review_service.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // This tells JPA: "Go into the 'reviewer' object and find the 'userID'"
    //List<Review> findByReviewer_UserID(Long userID);
    List<Review> findByReviewerID(Long reviewerID);
    // In ReviewRepository.java
    boolean existsByProposalIDAndReviewerID(Long proposalID, Long reviewerID);
    List<Review> findByProposalIDIn(List<Long> proposalIds);
}