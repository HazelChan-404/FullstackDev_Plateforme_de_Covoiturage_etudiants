package com.covoiturage.dto;

import com.covoiturage.model.Notification;

public class CreateNotificationDTO {
    private Long userId;
    private String title;
    private String message;
    private Notification.NotificationType notificationType;
    private Long relatedEntityId;
    
    public CreateNotificationDTO() {}
    
    public CreateNotificationDTO(Long userId, String title, String message, Notification.NotificationType notificationType) {
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.notificationType = notificationType;
    }
    
    public CreateNotificationDTO(Long userId, String title, String message, Notification.NotificationType notificationType, Long relatedEntityId) {
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.notificationType = notificationType;
        this.relatedEntityId = relatedEntityId;
    }
    
    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public Notification.NotificationType getNotificationType() { return notificationType; }
    public void setNotificationType(Notification.NotificationType notificationType) { this.notificationType = notificationType; }
    
    public Long getRelatedEntityId() { return relatedEntityId; }
    public void setRelatedEntityId(Long relatedEntityId) { this.relatedEntityId = relatedEntityId; }
}
