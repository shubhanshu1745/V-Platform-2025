package com.example.volunteer_platform.service;

import com.example.volunteer_platform.dto.ReviewDTO;
import com.example.volunteer_platform.entity.Review;
import com.example.volunteer_platform.entity.User;
import com.example.volunteer_platform.exception.ResourceNotFoundException;
import com.example.volunteer_platform.repository.ReviewRepository;
import com.example.volunteer_platform.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public ReviewDTO submitReview(ReviewDTO reviewDTO) {
        User reviewer = userRepository.findById(reviewDTO.getReviewerId())
                .orElseThrow(() -> new ResourceNotFoundException("Reviewer not found"));
        User reviewee = userRepository.findById(reviewDTO.getRevieweeId())
                .orElseThrow(() -> new ResourceNotFoundException("Reviewee not found"));

        Review review = modelMapper.map(reviewDTO, Review.class);
        review.setReviewer(reviewer);
        review.setReviewee(reviewee);
        review = reviewRepository.save(review);
        return modelMapper.map(review, ReviewDTO.class);
    }

    @Transactional(readOnly = true)
    public List<ReviewDTO> getReviewsForUser(Long userId) {
        List<Review> reviews = reviewRepository.findByReviewee_UserId(userId);
        return reviews.stream()
                .map(review -> modelMapper.map(review, ReviewDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new ResourceNotFoundException("Review not found with id: " + reviewId);
        }
        reviewRepository.deleteById(reviewId);
    }

    @Transactional(readOnly = true)
    public Double calculateUserReputation(Long userId) {
        List<Review> reviews = reviewRepository.findByReviewee_UserId(userId);

        if (reviews.isEmpty()) {
            return 0.0;
        }

        double totalRating = 0;
        for (Review review : reviews) {
            totalRating += review.getRating();
        }

        return totalRating / reviews.size();
    }
}