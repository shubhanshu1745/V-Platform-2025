package com.example.volunteer_platform.controller;

import com.example.volunteer_platform.dto.ReviewDTO;
import com.example.volunteer_platform.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    
    // Used to Review Submit Request ( Like a authenicated User can submit review )
    @PostMapping("/submit")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewDTO> submitReview(@RequestBody ReviewDTO reviewDTO) {
        ReviewDTO createdReview = reviewService.submitReview(reviewDTO);
        return new ResponseEntity<>(createdReview, HttpStatus.CREATED);
    }

    // Used to fetch the all reviews of a specific user ( used UserId) 
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsForUser(@PathVariable Long userId) {
        List<ReviewDTO> reviews = reviewService.getReviewsForUser(userId);
        return ResponseEntity.ok(reviews);
    }

    //Used to delete a review request 
    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}