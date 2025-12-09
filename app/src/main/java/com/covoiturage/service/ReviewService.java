package com.covoiturage.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.covoiturage.dto.ReviewDTO;
import com.covoiturage.dto.CreateReviewDTO;
import com.covoiturage.model.Review;
import com.covoiturage.model.User;
import com.covoiturage.model.Trip;
import com.covoiturage.repository.ReviewRepository;
import com.covoiturage.repository.UserRepository;
import com.covoiturage.repository.TripRepository;

@Service
public class ReviewService {
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TripRepository tripRepository;
    
    /**
     * Create a new review
     */
    @Transactional
    public ReviewDTO createReview(Long reviewerId, CreateReviewDTO createReviewDTO) {
        // Validate reviewer exists
        User reviewer = userRepository.findById(reviewerId)
            .orElseThrow(() -> new RuntimeException("Reviewer not found"));
        
        // Validate reviewed user exists
        User reviewedUser = userRepository.findById(createReviewDTO.getReviewedUserId())
            .orElseThrow(() -> new RuntimeException("Reviewed user not found"));
        
        // Validate trip if provided
        Trip trip = null;
        if (createReviewDTO.getTripId() != null) {
            trip = tripRepository.findById(createReviewDTO.getTripId())
                .orElseThrow(() -> new RuntimeException("Trip not found"));
        }
        
        // Validate rating
        if (createReviewDTO.getRating() == null || createReviewDTO.getRating() < 1 || createReviewDTO.getRating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }
        
        // Check if reviewer has already reviewed this user for this trip
        if (trip != null && reviewRepository.existsByReviewerIdAndReviewedUserIdAndTripId(
                reviewerId, createReviewDTO.getReviewedUserId(), createReviewDTO.getTripId())) {
            throw new RuntimeException("You have already reviewed this user for this trip");
        }
        
        // Cannot review yourself
        if (reviewerId.equals(createReviewDTO.getReviewedUserId())) {
            throw new RuntimeException("You cannot review yourself");
        }
        
        // Create review
        Review review = new Review();
        review.setReviewer(reviewer);
        review.setReviewedUser(reviewedUser);
        review.setTrip(trip);
        review.setRating(createReviewDTO.getRating());
        review.setComment(createReviewDTO.getComment());
        review.setReviewType(createReviewDTO.getReviewType());
        
        Review savedReview = reviewRepository.save(review);
        
        // Update user ratings
        updateUserRatings(createReviewDTO.getReviewedUserId());
        
        return new ReviewDTO(savedReview);
    }
    
    /**
     * Get review by ID
     */
    public ReviewDTO getReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review not found"));
        return new ReviewDTO(review);
    }
    
    /**
     * Get reviews by reviewer
     */
    public List<ReviewDTO> getReviewsByReviewer(Long reviewerId) {
        List<Review> reviews = reviewRepository.findByReviewerId(reviewerId);
        return reviews.stream()
            .map(ReviewDTO::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Get reviews for a user
     */
    public List<ReviewDTO> getReviewsForUser(Long userId) {
        List<Review> reviews = reviewRepository.findByReviewedUserId(userId);
        return reviews.stream()
            .map(ReviewDTO::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Get reviews by trip
     */
    public List<ReviewDTO> getReviewsByTrip(Long tripId) {
        List<Review> reviews = reviewRepository.findByTripId(tripId);
        return reviews.stream()
            .map(ReviewDTO::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Get all reviews involving a user (as reviewer or reviewed)
     */
    public List<ReviewDTO> getAllReviewsForUser(Long userId) {
        List<Review> reviews = reviewRepository.findAllReviewsForUser(userId);
        return reviews.stream()
            .map(ReviewDTO::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Update user ratings after a new review
     */
    @Transactional
    private void updateUserRatings(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Calculate driver rating
        Double avgDriverRating = reviewRepository.getAverageDriverRating(userId);
        Long driverCount = reviewRepository.countDriverReviews(userId);
        
        if (avgDriverRating != null) {
            user.setAverageRatingDriver(BigDecimal.valueOf(avgDriverRating).setScale(2, RoundingMode.HALF_UP));
            user.setTotalTripsDriver(driverCount.intValue());
        }
        
        // Calculate passenger rating
        Double avgPassengerRating = reviewRepository.getAveragePassengerRating(userId);
        Long passengerCount = reviewRepository.countPassengerReviews(userId);
        
        if (avgPassengerRating != null) {
            user.setAverageRatingPassenger(BigDecimal.valueOf(avgPassengerRating).setScale(2, RoundingMode.HALF_UP));
            user.setTotalTripsPassenger(passengerCount.intValue());
        }
        
        userRepository.save(user);
    }
    
    /**
     * Delete a review (only the reviewer can delete their own review)
     */
    @Transactional
    public void deleteReview(Long reviewId, Long reviewerId) {
        Review review = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review not found"));
        
        if (!review.getReviewer().getId().equals(reviewerId)) {
            throw new RuntimeException("You can only delete your own reviews");
        }
        
        Long reviewedUserId = review.getReviewedUser().getId();
        
        reviewRepository.delete(review);
        
        // Update user ratings
        updateUserRatings(reviewedUserId);
    }
}
