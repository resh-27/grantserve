package com.cognizant.review_service.controller;

import com.cognizant.review_service.dto.ReviewDto;
import com.cognizant.review_service.entity.Review;
import com.cognizant.review_service.service.IReviewService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


//@CrossOrigin(origins = "http://localhost:4200")
@Slf4j
@RestController
@RequestMapping("review")
public class ReviewController {

    @Autowired
    private IReviewService reviewService;

    // POST: Assign a new reviewer
    @PostMapping("/assign")
    public ResponseEntity<String> assign(@Valid @RequestBody ReviewDto reviewDto) {
        log.info("Controller: Received request to assign Reviewer ID {} to Proposal ID {}",
                reviewDto.reviewerId(), reviewDto.proposalId());

        String response = reviewService.assignReviewer(reviewDto);

        log.info("Controller: Review successfully assigned for Proposal ID: {}", reviewDto.proposalId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // GET: Retrieve all review records
    @GetMapping("/all")
    public ResponseEntity<List<Review>> getAll() {
        log.info("Controller: Fetching all reviews");
        List<Review> reviews = reviewService.getAllReviews();

        if (reviews.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    // GET: Reviewer Dashboard
    @GetMapping("/dashboard/{reviewerId}")
    public ResponseEntity<List<Review>> getDashboard(@PathVariable long reviewerId) {
        log.info("Controller: Fetching dashboard for Reviewer ID: {}", reviewerId);
        List<Review> reviews = reviewService.getReviewsByReviewer(reviewerId);

        if (reviews.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    // GET: Retrieve a single review by ID
    @GetMapping("/{id}")
    public ResponseEntity<Review> getById(@PathVariable long id) {
        log.info("Controller: Fetching individual Review ID: {}", id);
        Review review = reviewService.getReviewById(id);
        return new ResponseEntity<>(review, HttpStatus.OK);
    }

    // PUT: Update an existing review (Added to match the logic flow)
    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@PathVariable long id, @Valid @RequestBody ReviewDto reviewDto) {
        log.info("Controller: Request to update review record ID: {}", id);

        String response = reviewService.updateReview(id, reviewDto);

        log.info("Controller: Review record ID: {} updated successfully", id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // DELETE: Remove a review record
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable long id) {
        log.warn("Controller: Request to delete review record ID: {}", id);

        String response = reviewService.deleteReview(id);

        log.info("Controller: Review record ID {} deleted successfully", id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/application/{appId}")
    public List<Review> getReviews(@PathVariable Long appId) {
        return reviewService.getReviewsByApplications(appId);
    }
}