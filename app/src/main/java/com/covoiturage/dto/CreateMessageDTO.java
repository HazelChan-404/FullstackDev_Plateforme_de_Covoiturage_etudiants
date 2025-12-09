package com.covoiturage.dto;

public class CreateMessageDTO {
    private Long receiverId;
    private Long tripId;
    private String content;
    
    public CreateMessageDTO() {}
    
    public CreateMessageDTO(Long receiverId, String content) {
        this.receiverId = receiverId;
        this.content = content;
    }
    
    public CreateMessageDTO(Long receiverId, String content, Long tripId) {
        this.receiverId = receiverId;
        this.content = content;
        this.tripId = tripId;
    }
    
    // Getters and Setters
    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }
    
    public Long getTripId() { return tripId; }
    public void setTripId(Long tripId) { this.tripId = tripId; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
