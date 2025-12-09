package com.covoiturage.dto;

import com.covoiturage.model.Review;

public class CreateReviewDTO {
    private Long reviewedUserId;
    private Long tripId;
    private Integer rating;
    private String comment;
    private Review.ReviewType reviewType;
    
    public CreateReviewDTO() {}
    
    public CreateReviewDTO(Long reviewedUserId, Long tripId, Integer rating, String comment, Review.ReviewType reviewType) {
        this.reviewedUserId = reviewedUserId;
        this.tripId = tripId;
        this.rating = rating;
        this.comment = comment;
        this.reviewType = reviewType;
    }
    
    // Getters and Setters
    public Long getReviewedUserId() { return reviewedUserId; }
    public void setReviewedUserId(Long reviewedUserId) { this.reviewedUserId = reviewedUserId; }
    
    public Long getTripId() { return tripId; }
    public void setTripId(Long tripId) { this.tripId = tripId; }
    
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    
    public Review.ReviewType getReviewType() { return reviewType; }
    public void setReviewType(Review.ReviewType reviewType) { this.reviewType = reviewType; }
}
