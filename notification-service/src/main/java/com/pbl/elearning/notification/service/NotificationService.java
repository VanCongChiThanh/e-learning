package com.pbl.elearning.notification.service;

import com.google.firebase.messaging.*;
import com.pbl.elearning.notification.domain.DeviceToken;
import com.pbl.elearning.notification.domain.Notification;
import com.pbl.elearning.notification.domain.enums.NotificationType;
import com.pbl.elearning.notification.domain.enums.Platform;
import com.pbl.elearning.notification.payload.response.NotificationResponse;
import com.pbl.elearning.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final DeviceTokenService deviceTokenService;
    private final SimpMessagingTemplate messagingTemplate;
    private final FirebaseMessaging firebaseMessaging;

    /**
     * Gửi notification cho nhiều user
     */
    @Async("asyncExecutor")
    public void sendNotificationToUsers(List<UUID> userIds,
                                        NotificationType type,
                                        String title,
                                        String message,
                                        Map<String, String> metadata) {

        List<Notification> notifications = new ArrayList<>();

        // create notification records
        for (UUID userId : userIds) {
            Notification notif = new Notification();
            notif.setUserId(userId);
            notif.setType(type);
            notif.setTitle(title);
            notif.setMessage(message);
            notif.setMetadata(metadata);
            notif.setIsRead(false);
            notifications.add(notif);
        }
        notificationRepository.saveAll(notifications);

        //  Push WebSocket (broadcast or user-specific)
        for (Notification notif : notifications) {
            messagingTemplate.convertAndSendToUser(
                    notif.getUserId().toString(), "/queue/notifications", notif
            );
        }

        // Push Mobile (FCM)
        //  group token by topic
        Map<String, List<String>> platformTokens = new HashMap<>();
        for (UUID userId : userIds) {
            List<DeviceToken> tokens = deviceTokenService.getUserDevices(userId);
            for (DeviceToken token : tokens) {
                platformTokens
                        .computeIfAbsent(token.getPlatform().name(), k -> new ArrayList<>())
                        .add(token.getDeviceToken());
            }
        }

        // FCM by batch / topic
        platformTokens.forEach((platform, tokens) -> {
            tokens.forEach(token -> sendPushFCM(token, Platform.valueOf(platform), title, message, metadata));
        });
    }

    @Async("asyncExecutor")
    public void sendPushFCM(String token, Platform platform, String title, String body, Map<String, String> metadata){
        try {
            Message.Builder builder = Message.builder()
                    .setToken(token)
                    .putAllData(metadata)
                    .setNotification(com.google.firebase.messaging.Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build());

            if(platform == Platform.IOS){
                builder.setApnsConfig(ApnsConfig.builder()
                        .setAps(Aps.builder().setContentAvailable(true).build())
                        .build());
            }

            firebaseMessaging.send(builder.build());
        } catch (FirebaseMessagingException e){
            log.error("FCM send failed for token {}: {}", token, e.getMessage());
        }
    }

    // Lấy notification chưa đọc
    public Page<NotificationResponse> getAllNotifications(UUID userId, boolean onlyUnread, Pageable pageable){
        if(!onlyUnread){
            Page<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId,pageable);
            return notifications.map(NotificationResponse::toResponse);
        }
        Page<Notification> notifications = notificationRepository.findByUserIdAndIsReadFalse(userId,pageable);
        return notifications.map(NotificationResponse::toResponse);
    }
    //Đánh dấu đã đọc
    public UUID markAsRead(UUID notificationId){
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setIsRead(true);
            notificationRepository.save(n);
        });
        return notificationId;
    }

}