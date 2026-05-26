package com.cognizant.review_service.util;

import com.cognizant.review_service.dto.ReviewDto;
import com.cognizant.review_service.entity.Review;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ClassUtilSeparator {


    public static Review reviewUtil(ReviewDto dto) {
        Review review = new Review();
        review.setProposalID(dto.proposalId());
        review.setReviewerID(dto.reviewerId());
        review.setScore(dto.score());
        review.setComments(dto.comments());
        review.setDate(dto.date());
        review.setStatus(dto.status());
        return review;
    }


}
