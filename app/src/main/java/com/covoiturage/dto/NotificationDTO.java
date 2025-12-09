package com.covoiturage.dto;

import java.time.LocalDateTime;

import com.covoiturage.model.Notification;

public class NotificationDTO {
    private Long id;
    private UserDTO user;
    private String title;
    private String message;
    private Notification.NotificationType notificationType;
    private Boolean isRead;
    private Long relatedEntityId;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    
    // Constructors
    public NotificationDTO() {}
    
    public NotificationDTO(Notification notification) {
        this.id = notification.getId();
        this.user = notification.getUser() != null ? new UserDTO(notification.getUser()) : null;
        this.title = notification.getTitle();
        this.message = notification.getMessage();
        this.notificationType = notification.getNotificationType();
        this.isRead = notification.getIsRead();
        this.relatedEntityId = notification.getRelatedEntityId();
        this.createdAt = notification.getCreatedAt();
        this.readAt = notification.getReadAt();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public UserDTO getUser() { return user; }
    public void setUser(UserDTO user) { this.user = user; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public Notification.NotificationType getNotificationType() { return notificationType; }
    public void setNotificationType(Notification.NotificationType notificationType) { this.notificationType = notificationType; }
    
    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }
    
    public Long getRelatedEntityId() { return relatedEntityId; }
    public void setRelatedEntityId(Long relatedEntityId) { this.relatedEntityId = relatedEntityId; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }
}
