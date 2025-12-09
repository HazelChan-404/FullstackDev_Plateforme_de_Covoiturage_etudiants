package com.covoiturage.dto;

import java.time.LocalDateTime;

import com.covoiturage.model.Message;

public class MessageDTO {
    private Long id;
    private UserDTO sender;
    private UserDTO receiver;
    private TripDTO trip;
    private String content;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public MessageDTO() {}
    
    public MessageDTO(Message message) {
        this.id = message.getId();
        this.sender = message.getSender() != null ? new UserDTO(message.getSender()) : null;
        this.receiver = message.getReceiver() != null ? new UserDTO(message.getReceiver()) : null;
        this.trip = message.getTrip() != null ? new TripDTO(message.getTrip()) : null;
        this.content = message.getContent();
        this.isRead = message.getIsRead();
        this.createdAt = message.getCreatedAt();
        this.updatedAt = message.getUpdatedAt();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public UserDTO getSender() { return sender; }
    public void setSender(UserDTO sender) { this.sender = sender; }
    
    public UserDTO getReceiver() { return receiver; }
    public void setReceiver(UserDTO receiver) { this.receiver = receiver; }
    
    public TripDTO getTrip() { return trip; }
    public void setTrip(TripDTO trip) { this.trip = trip; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
