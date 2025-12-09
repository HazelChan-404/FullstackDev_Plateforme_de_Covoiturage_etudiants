package com.covoiturage.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.covoiturage.dto.NotificationDTO;
import com.covoiturage.dto.CreateNotificationDTO;
import com.covoiturage.model.Notification;
import com.covoiturage.model.User;
import com.covoiturage.repository.NotificationRepository;
import com.covoiturage.repository.UserRepository;

@Service
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Create a new notification
     */
    @Transactional
    public NotificationDTO createNotification(CreateNotificationDTO createNotificationDTO) {
        // Validate user exists
        User user = userRepository.findById(createNotificationDTO.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Validate title and message
        if (createNotificationDTO.getTitle() == null || createNotificationDTO.getTitle().trim().isEmpty()) {
            throw new RuntimeException("Notification title cannot be empty");
        }
        if (createNotificationDTO.getMessage() == null || createNotificationDTO.getMessage().trim().isEmpty()) {
            throw new RuntimeException("Notification message cannot be empty");
        }
        
        // Create notification
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(createNotificationDTO.getTitle().trim());
        notification.setMessage(createNotificationDTO.getMessage().trim());
        notification.setNotificationType(createNotificationDTO.getNotificationType());
        notification.setRelatedEntityId(createNotificationDTO.getRelatedEntityId());
        notification.setIsRead(false);
        
        Notification savedNotification = notificationRepository.save(notification);
        
        return new NotificationDTO(savedNotification);
    }
    
    /**
     * Get notification by ID
     */
    public NotificationDTO getNotificationById(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new RuntimeException("Notification not found"));
        return new NotificationDTO(notification);
    }
    
    /**
     * Get notifications for user
     */
    public List<NotificationDTO> getNotificationsForUser(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId);
        return notifications.stream()
            .map(NotificationDTO::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Get notifications for user ordered by creation date (most recent first)
     */
    public List<NotificationDTO> getNotificationsForUserOrderByDate(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return notifications.stream()
            .map(NotificationDTO::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Get unread notifications for user
     */
    public List<NotificationDTO> getUnreadNotificationsForUser(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsReadFalse(userId);
        return notifications.stream()
            .map(NotificationDTO::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Get unread notifications for user ordered by creation date
     */
    public List<NotificationDTO> getUnreadNotificationsForUserOrderByDate(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        return notifications.stream()
            .map(NotificationDTO::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Get notifications by type
     */
    public List<NotificationDTO> getNotificationsByType(Notification.NotificationType notificationType) {
        List<Notification> notifications = notificationRepository.findByNotificationType(notificationType);
        return notifications.stream()
            .map(NotificationDTO::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Get notifications for user by type
     */
    public List<NotificationDTO> getNotificationsForUserByType(Long userId, Notification.NotificationType notificationType) {
        List<Notification> notifications = notificationRepository.findByUserIdAndNotificationType(userId, notificationType);
        return notifications.stream()
            .map(NotificationDTO::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Get recent notifications for user
     */
    public List<NotificationDTO> getRecentNotificationsForUser(Long userId, Integer limit) {
        List<Notification> notifications = notificationRepository.findRecentNotifications(userId);
        return notifications.stream()
            .limit(limit != null ? limit : 10)
            .map(NotificationDTO::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Get notifications related to a specific entity
     */
    public List<NotificationDTO> getNotificationsByRelatedEntity(Long relatedEntityId) {
        List<Notification> notifications = notificationRepository.findByRelatedEntityId(relatedEntityId);
        return notifications.stream()
            .map(NotificationDTO::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Get count of unread notifications for user
     */
    public Long getUnreadNotificationCount(Long userId) {
        return notificationRepository.countUnreadNotifications(userId);
    }
    
    /**
     * Mark notification as read
     */
    @Transactional
    public NotificationDTO markNotificationAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        // Only the notification owner can mark it as read
        if (!notification.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only mark your own notifications as read");
        }
        
        notification.setIsRead(true);
        notification.setReadAt(LocalDateTime.now());
        
        Notification savedNotification = notificationRepository.save(notification);
        
        return new NotificationDTO(savedNotification);
    }
    
    /**
     * Mark multiple notifications as read
     */
    @Transactional
    public void markMultipleNotificationsAsRead(List<Long> notificationIds, Long userId) {
        for (Long notificationId : notificationIds) {
            try {
                markNotificationAsRead(notificationId, userId);
            } catch (RuntimeException e) {
                // Skip notifications that don't belong to the user
                continue;
            }
        }
    }
    
    /**
     * Mark all notifications as read for user
     */
    @Transactional
    public void markAllNotificationsAsReadForUser(Long userId) {
        List<Notification> unreadNotifications = notificationRepository.findByUserIdAndIsReadFalse(userId);
        
        for (Notification notification : unreadNotifications) {
            notification.setIsRead(true);
            notification.setReadAt(LocalDateTime.now());
        }
        
        notificationRepository.saveAll(unreadNotifications);
    }
    
    /**
     * Delete a notification (only owner can delete)
     */
    @Transactional
    public void deleteNotification(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new RuntimeException("Notification not found"));
        
        if (!notification.getUser().getId().equals(userId)) {
            throw new RuntimeException("You can only delete your own notifications");
        }
        
        notificationRepository.delete(notification);
    }
    
    /**
     * Delete old read notifications (cleanup task)
     */
    @Transactional
    public void cleanupOldReadNotifications(LocalDateTime cutoffDate) {
        List<Notification> oldNotifications = notificationRepository.findOldReadNotifications(cutoffDate);
        notificationRepository.deleteAll(oldNotifications);
    }
    
    /**
     * Convenience method to create common notification types
     */
    public NotificationDTO createBookingRequestNotification(Long userId, Long bookingId, String message) {
        CreateNotificationDTO dto = new CreateNotificationDTO(
            userId, 
            "Nouvelle demande de réservation", 
            message, 
            Notification.NotificationType.BOOKING_REQUEST, 
            bookingId
        );
        return createNotification(dto);
    }
    
    public NotificationDTO createBookingConfirmedNotification(Long userId, Long bookingId, String message) {
        CreateNotificationDTO dto = new CreateNotificationDTO(
            userId, 
            "Réservation confirmée", 
            message, 
            Notification.NotificationType.BOOKING_CONFIRMED, 
            bookingId
        );
        return createNotification(dto);
    }
    
    public NotificationDTO createMessageReceivedNotification(Long userId, Long messageId, String message) {
        CreateNotificationDTO dto = new CreateNotificationDTO(
            userId, 
            "Nouveau message reçu", 
            message, 
            Notification.NotificationType.MESSAGE_RECEIVED, 
            messageId
        );
        return createNotification(dto);
    }
    
    public NotificationDTO createReviewReceivedNotification(Long userId, Long reviewId, String message) {
        CreateNotificationDTO dto = new CreateNotificationDTO(
            userId, 
            "Nouvelle avis reçu", 
            message, 
            Notification.NotificationType.REVIEW_RECEIVED, 
            reviewId
        );
        return createNotification(dto);
    }
}
