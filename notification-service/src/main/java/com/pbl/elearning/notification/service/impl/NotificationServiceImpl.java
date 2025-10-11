package com.pbl.elearning.notification.service.impl;

import com.google.firebase.messaging.*;
import com.pbl.elearning.notification.domain.DeviceToken;
import com.pbl.elearning.notification.domain.Notification;
import com.pbl.elearning.notification.domain.enums.NotificationType;
import com.pbl.elearning.notification.domain.enums.Platform;
import com.pbl.elearning.notification.payload.response.NotificationResponse;
import com.pbl.elearning.notification.repository.NotificationRepository;
import com.pbl.elearning.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final DeviceTokenServiceImpl deviceTokenService;
    private final SimpMessagingTemplate messagingTemplate;
    private final FirebaseMessaging firebaseMessaging;

    /**
     * Gửi notification cho nhiều user
     */
    @Override
    @Async("asyncExecutor")
    public void sendNotificationToUsers(List<UUID> userIds,
                                        NotificationType type,
                                        String title,
                                        String message,
                                        Map<String, String> metadata) {
        try {
            // 1. Persist notifications
            List<Notification> notifications = createNotifications(userIds, type, title, message, metadata);
            notificationRepository.saveAll(notifications);

            // 2. Send via WebSocket
            sendWebSocketNotifications(notifications);

            // 3. Send via FCM
            sendFcmNotifications(userIds, title, message, metadata);

        } catch (Exception e) {
            log.error("Failed to send notifications to users: {}", userIds, e);
        }
    }

    /**
     * Gửi FCM multicast (tối đa 500 tokens) - Async
     */

    @Async("asyncExecutor")
    public CompletableFuture<BatchResponse> sendMulticastFcm(Platform platform,
                                                             List<String> tokens,
                                                             String title,
                                                             String body,
                                                             Map<String, String> metadata) {
        try {
            MulticastMessage.Builder builder = MulticastMessage.builder()
                    .addAllTokens(tokens)
                    .putAllData(metadata != null ? metadata : Map.of())
                    .setNotification(com.google.firebase.messaging.Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build());

            // Platform-specific config
            if (platform == Platform.IOS) {
                builder.setApnsConfig(ApnsConfig.builder()
                        .setAps(Aps.builder()
                                .setContentAvailable(true)
                                .setSound("default")
                                .build())
                        .build());
            } else if (platform == Platform.ANDROID) {
                builder.setAndroidConfig(AndroidConfig.builder()
                        .setPriority(AndroidConfig.Priority.HIGH)
                        .build());
            }

            BatchResponse response = firebaseMessaging.sendMulticast(builder.build());

            log.info("FCM multicast sent: {} success, {} failed",
                    response.getSuccessCount(),
                    response.getFailureCount());

            // Handle failed tokens
            handleFailedTokens(tokens, response);

            return CompletableFuture.completedFuture(response);

        } catch (FirebaseMessagingException e) {
            log.error("Failed to send multicast FCM for platform: {}", platform, e);
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    public Page<NotificationResponse> getAllNotifications(UUID userId, boolean onlyUnread, Pageable pageable) {
        Page<Notification> notifications = onlyUnread
                ? notificationRepository.findByUserIdAndIsReadFalse(userId, pageable)
                : notificationRepository.findByUserId(userId, pageable);

        return notifications.map(NotificationResponse::toResponse);
    }
   @Override
    public UUID markAsRead(UUID notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setIsRead(true);
            notificationRepository.save(n);
        });
        return notificationId;
    }
    @Override
    public long countUnreadNotifications(UUID userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }
    @Override
    public void deleteNotification(UUID notificationId){
        notificationRepository.deleteById(notificationId);
    }

    private List<Notification> createNotifications(List<UUID> userIds,
                                                   NotificationType type,
                                                   String title,
                                                   String message,
                                                   Map<String, String> metadata) {
        return userIds.stream()
                .map(userId -> {
                    Notification notification = new Notification();
                    notification.setUserId(userId);
                    notification.setType(type);
                    notification.setTitle(title);
                    notification.setMessage(message);
                    notification.setMetadata(metadata);
                    notification.setIsRead(false);
                    return notification;
                })
                .collect(Collectors.toList());
    }

    private void sendWebSocketNotifications(List<Notification> notifications) {
        notifications.forEach(notification -> {
            try {
                NotificationResponse response = NotificationResponse.toResponse(notification);
                UUID userId = response.getUserId();
                log.info("🚀 [WebSocket] Sending notification:");
                log.info("   👤 userId: {}", userId);
                log.info("   📦 title: {}", response.getTitle());
                log.info("   📦 message: {}", response.getMessage());
                messagingTemplate.convertAndSendToUser(
                        response.getUserId().toString(),
                        "/queue/notifications",
                        response
                );
            } catch (Exception e) {
                log.warn("Failed to send WebSocket notification to user: {}",
                        notification.getUserId(), e);
            }
        });
    }

    private void sendFcmNotifications(List<UUID> userIds,
                                      String title,
                                      String message,
                                      Map<String, String> metadata) {
        // Fetch all device tokens in one query (avoid N+1)
        Map<Platform, List<String>> tokensByPlatform =
                deviceTokenService.getUserDevicesByUserIds(userIds).stream()
                        .collect(Collectors.groupingBy(
                                DeviceToken::getPlatform,
                                Collectors.mapping(DeviceToken::getDeviceToken, Collectors.toList())
                        ));

        // Send FCM in parallel for all platforms
        List<CompletableFuture<Void>> futures = tokensByPlatform.entrySet().stream()
                .map(entry -> CompletableFuture.runAsync(() ->
                        sendFcmBatch(entry.getKey(), entry.getValue(), title, message, metadata)
                ))
                .collect(Collectors.toList());
        //fire and forget
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .exceptionally(ex -> {
                    log.warn("Some FCM tasks failed: {}", ex.getMessage());
                    return null;
                });

    }

    private void sendFcmBatch(Platform platform,
                              List<String> tokens,
                              String title,
                              String message,
                              Map<String, String> metadata) {
        if (tokens.isEmpty()) {
            return;
        }

        // FCM supports sending to multiple tokens at once (up to 500)
        int batchSize = 500;
        List<CompletableFuture<BatchResponse>> futures = new ArrayList<>();

        for (int i = 0; i < tokens.size(); i += batchSize) {
            List<String> batch = tokens.subList(i, Math.min(i + batchSize, tokens.size()));
            futures.add(sendMulticastFcm(platform, batch, title, message, metadata));
        }

        // Wait for all batches to complete
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        } catch (Exception e) {
            log.error("Failed to send FCM notifications for platform: {}", platform, e);
        }
    }

    /**
     * Xử lý tokens thất bại (invalid/expired)
     */
    private void handleFailedTokens(List<String> tokens, BatchResponse response) {
        if (response.getFailureCount() == 0) {
            return;
        }

        List<SendResponse> responses = response.getResponses();
        for (int i = 0; i < responses.size(); i++) {
            if (!responses.get(i).isSuccessful()) {
                String failedToken = tokens.get(i);
                FirebaseMessagingException exception = responses.get(i).getException();

                if (exception != null) {
                    String errorCode = String.valueOf(exception.getErrorCode());

                    // Token không hợp lệ - xóa khỏi DB
                    if ("registration-token-not-registered".equals(errorCode) ||
                            "invalid-registration-token".equals(errorCode) ||
                            "unregistered".equals(errorCode)) {

                        log.warn("Invalid FCM token, removing: {}", failedToken);
                        deviceTokenService.removeToken(failedToken);
                    }
                }
            }
        }
    }
}