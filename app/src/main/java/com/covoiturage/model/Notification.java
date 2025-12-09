package com.covoiturage.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;
    
    @Column(name = "notification_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;
    
    @Column(name = "is_read")
    private Boolean isRead = false;
    
    @Column(name = "related_entity_id")
    private Long relatedEntityId;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "read_at")
    private LocalDateTime readAt;
    
    public enum NotificationType {
        BOOKING_REQUEST,
        BOOKING_CONFIRMED,
        BOOKING_CANCELLED,
        TRIP_REMINDER,
        MESSAGE_RECEIVED,
        REVIEW_RECEIVED,
        TRIP_COMPLETED,
        PAYMENT_RECEIVED,
        SYSTEM_NOTIFICATION
    }
    
    // Constructors
    public Notification() {}
    
    public Notification(User user, String title, String message, NotificationType notificationType) {
        this.user = user;
        this.title = title;
        this.message = message;
        this.notificationType = notificationType;
    }
    
    public Notification(User user, String title, String message, NotificationType notificationType, Long relatedEntityId) {
        this.user = user;
        this.title = title;
        this.message = message;
        this.notificationType = notificationType;
        this.relatedEntityId = relatedEntityId;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public NotificationType getNotificationType() { return notificationType; }
    public void setNotificationType(NotificationType notificationType) { this.notificationType = notificationType; }
    
    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }
    
    public Long getRelatedEntityId() { return relatedEntityId; }
    public void setRelatedEntityId(Long relatedEntityId) { this.relatedEntityId = relatedEntityId; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }
}
