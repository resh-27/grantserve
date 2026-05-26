package com.cognizant.review_service.service;

import com.cognizant.review_service.dto.ReviewDto;
import com.cognizant.review_service.dto.UserResponseDto;
import com.cognizant.review_service.entity.Review;
import com.cognizant.review_service.exception.ReviewNotFoundException;
import com.cognizant.review_service.feignclients.ProposalFeignClient;
import com.cognizant.review_service.feignclients.UserFeignClient;
import com.cognizant.review_service.repository.ReviewRepository;
import com.cognizant.review_service.util.ClassUtilSeparator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ReviewServiceImpl implements IReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProposalFeignClient proposalFeignClient;

    @Autowired
    private UserFeignClient userFeignClient;


    @Override
    public String assignReviewer(ReviewDto reviewDto) {
        log.info("Service: Assigning Reviewer ID {} to Proposal ID {}", reviewDto.reviewerId(), reviewDto.proposalId());

        // 1. Check if Proposal exists
        Boolean exists = proposalFeignClient.proposalExist(reviewDto.proposalId());
        if (exists == null || !exists) {
            throw new RuntimeException("Proposal not found");
        }

        // 2. Validate Reviewer
        UserResponseDto userData = userFeignClient.getUserById(reviewDto.reviewerId());
        if (userData == null || !"REVIEWER".equalsIgnoreCase(userData.role())) {
            throw new RuntimeException("User not found or is not a REVIEWER");
        }

        // 3. DUPLICATE CHECK
        boolean alreadyAssigned = reviewRepository.existsByProposalIDAndReviewerID(
                reviewDto.proposalId(),
                reviewDto.reviewerId()
        );

        if (alreadyAssigned) {
            return "Reviewer is already assigned to this proposal";
        }

        // 4. SYNC STATUS (Fixing the Enum Incompatibility)
        // We use the String "UNDER_REVIEW" but the Feign client might expect String
        boolean isUpdated = proposalFeignClient.updateStatusById("UNDER_REVIEW", reviewDto.proposalId());

        if (!isUpdated) {
            throw new RuntimeException("Remote Proposal status update failed");
        }

        // 5. SAVE: Mapping DTO to Entity
        Review review = ClassUtilSeparator.reviewUtil(reviewDto);

        // FIX: Convert String to ReviewStatus Enum
        // Ensure "UNDER_REVIEW" exists in your ReviewStatus enum file
        review.setStatus(com.cognizant.review_service.enums.ReviewStatus.valueOf("UNDER_REVIEW"));

        reviewRepository.save(review);

        log.info("Service: Assignment successful");
        return "Review assigned successfully";
    }
    @Override
    public List<Review> getAllReviews() {
        log.info("Service: Fetching all review records");
        return reviewRepository.findAll();
    }

    @Override
    public List<Review> getReviewsByReviewer(long reviewerId) {
        log.info("Service: Fetching reviews for Reviewer ID: {}", reviewerId);
        List<Review> reviews = reviewRepository.findByReviewerID(reviewerId);

        if (reviews.isEmpty()) {
            log.warn("Service: No reviews found for Reviewer ID: {}", reviewerId);
            throw new ReviewNotFoundException("No reviews found for this reviewer", HttpStatus.NOT_FOUND);
        }
        return reviews;
    }

    @Override
    public Review getReviewById(long id) {
        log.info("Service: Fetching individual Review ID: {}", id);
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Review ID " + id + " not found", HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional
    public String updateReview(long id, ReviewDto reviewDto) {
        log.info("Service: Updating Review ID: {} and syncing with Proposal ID: {}", id, reviewDto.proposalId());

        Review existingReview = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Review ID " + id + " not found", HttpStatus.NOT_FOUND));

        String statusResult = String.valueOf(reviewDto.status());
        boolean isUpdated = proposalFeignClient.updateStatusById(statusResult, reviewDto.proposalId());

        if (!isUpdated) {
            log.error("Service Error: Failed to update status for Proposal ID: {}", reviewDto.proposalId());
            throw new RuntimeException("Remote Proposal status update failed");
        }

        existingReview.setScore(reviewDto.score());
        existingReview.setComments(reviewDto.comments());
        existingReview.setStatus(reviewDto.status());
        existingReview.setDate(reviewDto.date());

        reviewRepository.save(existingReview);

        log.info("Service: Review ID {} updated and Proposal status synced to {}", id, statusResult);
        return "Review updated. Proposal status updated to " + statusResult;
    }

    @Override
    @Transactional
    public String deleteReview(long id) {
        log.info("Service: Deleting review record ID: {}", id);

        Review review = getReviewById(id);
        reviewRepository.delete(review);

        return "Review deleted successfully";
    }


    public List<Review> getReviewsByApplications(Long appId) {
        // 1. Call the Proposal Service via Feign
        // Feign handles the JSON conversion and URL building automatically
        ResponseEntity<List<Long>> response = proposalFeignClient.getIds(appId);
        List<Long> pIds = response.getBody();

        // 2. Fetch from local Review DB
        if (pIds != null && !pIds.isEmpty()) {
            // This uses the IN clause we discussed earlier
            return reviewRepository.findByProposalIDIn(pIds);
        }

        return new ArrayList<>();
    }
}