package com.cognizant.review_service.service;

import com.cognizant.review_service.dto.ReviewDto;
import com.cognizant.review_service.entity.Review;
import java.util.List;

public interface IReviewService {
    String assignReviewer(ReviewDto reviewDto);
    List<Review> getAllReviews(); // Added to match Controller
    List<Review> getReviewsByReviewer(long reviewerId);
    Review getReviewById(long id);
    String updateReview(long id, ReviewDto reviewDto); // Uncommented
    String deleteReview(long id);
    public List<Review> getReviewsByApplications(Long appId);
}