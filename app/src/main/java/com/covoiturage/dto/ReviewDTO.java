package com.covoiturage.dto;

import java.time.LocalDateTime;

import com.covoiturage.model.Review;

public class ReviewDTO {
    private Long id;
    private UserDTO reviewer;
    private UserDTO reviewedUser;
    private TripDTO trip;
    private Integer rating;
    private String comment;
    private Review.ReviewType reviewType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public ReviewDTO() {}
    
    public ReviewDTO(Review review) {
        this.id = review.getId();
        this.reviewer = review.getReviewer() != null ? new UserDTO(review.getReviewer()) : null;
        this.reviewedUser = review.getReviewedUser() != null ? new UserDTO(review.getReviewedUser()) : null;
        this.trip = review.getTrip() != null ? new TripDTO(review.getTrip()) : null;
        this.rating = review.getRating();
        this.comment = review.getComment();
        this.reviewType = review.getReviewType();
        this.createdAt = review.getCreatedAt();
        this.updatedAt = review.getUpdatedAt();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public UserDTO getReviewer() { return reviewer; }
    public void setReviewer(UserDTO reviewer) { this.reviewer = reviewer; }
    
    public UserDTO getReviewedUser() { return reviewedUser; }
    public void setReviewedUser(UserDTO reviewedUser) { this.reviewedUser = reviewedUser; }
    
    public TripDTO getTrip() { return trip; }
    public void setTrip(TripDTO trip) { this.trip = trip; }
    
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    
    public Review.ReviewType getReviewType() { return reviewType; }
    public void setReviewType(Review.ReviewType reviewType) { this.reviewType = reviewType; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
