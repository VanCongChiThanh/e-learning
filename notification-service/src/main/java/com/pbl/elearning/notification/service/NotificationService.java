package com.pbl.elearning.notification.service;

import com.pbl.elearning.notification.domain.enums.NotificationType;
import com.pbl.elearning.notification.payload.response.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface NotificationService {
    void sendNotificationToUsers(List<UUID> userIds, NotificationType type, String title, String message, Map<String, String> metadata);
    Page<NotificationResponse> getAllNotifications(UUID userId, boolean onlyUnread, Pageable pageable);
    UUID markAsRead(UUID notificationId);
    long countUnreadNotifications(UUID userId);
    void deleteNotification(UUID notificationId);
}