package com.pbl.elearning.web.endpoint.notification;

import com.pbl.elearning.common.payload.general.ResponseDataAPI;
import com.pbl.elearning.notification.domain.enums.NotificationType;
import com.pbl.elearning.notification.payload.request.RegisterDeviceRequest;
import com.pbl.elearning.notification.service.DeviceTokenService;
import com.pbl.elearning.notification.service.NotificationService;
import com.pbl.elearning.security.annotation.CurrentUser;
import com.pbl.elearning.security.domain.UserPrincipal;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/v1/notifications")
@RestController
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final DeviceTokenService deviceTokenService;
    @GetMapping("/all")
    @ApiOperation("get all notifications")
    public ResponseEntity<ResponseDataAPI> getNotifications(
            @RequestParam boolean isRead,
            @CurrentUser UserPrincipal userPrincipal
    ){
        return ResponseEntity.ok(ResponseDataAPI.successWithoutMeta(notificationService.getAllNotifications(userPrincipal.getId(),isRead)));
    }
    @PostMapping("/device-token")
    @ApiOperation("Save device token for push notifications")
    public ResponseEntity<ResponseDataAPI> saveDeviceToken(
            @RequestParam RegisterDeviceRequest request,
            @CurrentUser UserPrincipal userPrincipal
    ){
        return ResponseEntity.ok(ResponseDataAPI
                .successWithoutMeta(deviceTokenService.registerDevice(userPrincipal.getId(), request.getDeviceToken(), request.getPlatform())));
    }
    @PostMapping("/read/{notificationId}")
    @ApiOperation("Mark notification as read")
    public ResponseEntity<ResponseDataAPI> markAsRead(
            @PathVariable UUID notificationId
    ){
        return ResponseEntity.ok(ResponseDataAPI
                .successWithoutMeta(notificationService.markAsRead(notificationId)));
    }
    @PostMapping("/test")
    @ApiOperation("Send test notification to user")
    public ResponseEntity<ResponseDataAPI> sendTestNotification(
            @CurrentUser UserPrincipal userPrincipal
    ){
        notificationService.sendNotificationToUsers(
                java.util.List.of(userPrincipal.getId()),
                NotificationType.SYSTEM_ALERT,
                "Test Notification",
                "This is a test notification",
                java.util.Map.of("key", "value")
        );
        return ResponseEntity.ok(ResponseDataAPI.successWithoutMeta("Test notification sent"));
    }
}