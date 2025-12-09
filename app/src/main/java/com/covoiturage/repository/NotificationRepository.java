package com.covoiturage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.covoiturage.model.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // Find notifications by user
    List<Notification> findByUserId(Long userId);
    
    // Find unread notifications by user
    List<Notification> findByUserIdAndIsReadFalse(Long userId);
    
    // Find notifications by user ordered by creation date (most recent first)
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    // Find unread notifications by user ordered by creation date
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);
    
    // Find notifications by type
    List<Notification> findByNotificationType(Notification.NotificationType notificationType);
    
    // Find notifications by user and type
    List<Notification> findByUserIdAndNotificationType(Long userId, Notification.NotificationType notificationType);
    
    // Count unread notifications for a user
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.isRead = false")
    Long countUnreadNotifications(@Param("userId") Long userId);
    
    // Find notifications related to a specific entity (trip, booking, etc.)
    List<Notification> findByRelatedEntityId(Long relatedEntityId);
    
    // Find notifications by user and related entity
    List<Notification> findByUserIdAndRelatedEntityId(Long userId, Long relatedEntityId);
    
    // Find recent notifications for a user (limit can be applied in service)
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId ORDER BY n.createdAt DESC")
    List<Notification> findRecentNotifications(@Param("userId") Long userId);
    
    // Find notifications older than a specific date (for cleanup)
    @Query("SELECT n FROM Notification n WHERE n.createdAt < :date AND n.isRead = true")
    List<Notification> findOldReadNotifications(@Param("date") java.time.LocalDateTime date);
}
