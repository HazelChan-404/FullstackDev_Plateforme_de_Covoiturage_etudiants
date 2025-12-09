package com.covoiturage.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.covoiturage.model.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    // Find reviews by reviewer
    List<Review> findByReviewerId(Long reviewerId);
    
    // Find reviews by reviewed user
    List<Review> findByReviewedUserId(Long reviewedUserId);
    
    // Find reviews by trip
    List<Review> findByTripId(Long tripId);
    
    // Find reviews by review type
    List<Review> findByReviewType(Review.ReviewType reviewType);
    
    // Check if user has already reviewed another user for a specific trip
    boolean existsByReviewerIdAndReviewedUserIdAndTripId(Long reviewerId, Long reviewedUserId, Long tripId);
    
    // Find review by reviewer, reviewed user, and trip
    Optional<Review> findByReviewerIdAndReviewedUserIdAndTripId(Long reviewerId, Long reviewedUserId, Long tripId);
    
    // Get average rating for a user as driver
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.reviewedUser.id = :userId AND r.reviewType = 'DRIVER'")
    Double getAverageDriverRating(@Param("userId") Long userId);
    
    // Get average rating for a user as passenger
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.reviewedUser.id = :userId AND r.reviewType = 'PASSENGER'")
    Double getAveragePassengerRating(@Param("userId") Long userId);
    
    // Count reviews for a user as driver
    @Query("SELECT COUNT(r) FROM Review r WHERE r.reviewedUser.id = :userId AND r.reviewType = 'DRIVER'")
    Long countDriverReviews(@Param("userId") Long userId);
    
    // Count reviews for a user as passenger
    @Query("SELECT COUNT(r) FROM Review r WHERE r.reviewedUser.id = :userId AND r.reviewType = 'PASSENGER'")
    Long countPassengerReviews(@Param("userId") Long userId);
    
    // Find all reviews where the current user is either reviewer or reviewed user
    @Query("SELECT r FROM Review r WHERE r.reviewer.id = :userId OR r.reviewedUser.id = :userId ORDER BY r.createdAt DESC")
    List<Review> findAllReviewsForUser(@Param("userId") Long userId);
}
