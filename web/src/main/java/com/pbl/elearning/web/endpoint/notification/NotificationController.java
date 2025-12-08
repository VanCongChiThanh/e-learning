package com.pbl.elearning.web.endpoint.notification;

import com.pbl.elearning.common.payload.general.PageInfo;
import com.pbl.elearning.common.payload.general.ResponseDataAPI;
import com.pbl.elearning.common.util.PagingUtils;
import com.pbl.elearning.notification.domain.enums.NotificationType;
import com.pbl.elearning.notification.payload.request.PaymentNotificationRequest;
import com.pbl.elearning.notification.payload.request.RegisterDeviceRequest;
import com.pbl.elearning.notification.payload.response.NotificationResponse;
import com.pbl.elearning.notification.service.DeviceTokenService;
import com.pbl.elearning.notification.service.NotificationService;
import com.pbl.elearning.security.annotation.CurrentUser;
import com.pbl.elearning.security.domain.UserPrincipal;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequestMapping("/v1/notifications")
@RestController
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
        private final NotificationService notificationService;
        private final DeviceTokenService deviceTokenService;

        @GetMapping
        @ApiOperation("get all notifications")
        public ResponseEntity<ResponseDataAPI> getNotifications(
                        @RequestParam(value = "only_unread", defaultValue = "false") boolean onlyUnread,
                        @RequestParam(value = "page", defaultValue = "1") int page,
                        @RequestParam(value = "paging", defaultValue = "6") int paging,
                        @RequestParam(value = "sort", defaultValue = "created_at") String sort,
                        @RequestParam(value = "order", defaultValue = "desc") String order,
                        @CurrentUser UserPrincipal userPrincipal) {
                Pageable pageable = PagingUtils.makePageRequest(sort, order, page, paging);
                Page<NotificationResponse> notifications = notificationService.getAllNotifications(
                                userPrincipal.getId(),
                                onlyUnread,
                                pageable);
                PageInfo pageInfo = new PageInfo(
                                pageable.getPageNumber() + 1,
                                notifications.getTotalPages(),
                                notifications.getTotalElements());
                return ResponseEntity.ok(ResponseDataAPI
                                .success(notifications.getContent(), pageInfo));
        }

        @PostMapping("/device-token")
        @ApiOperation("Save device token for push notifications")
        public ResponseEntity<ResponseDataAPI> saveDeviceToken(
                        @RequestBody RegisterDeviceRequest request,
                        @CurrentUser UserPrincipal userPrincipal) {
                return ResponseEntity.ok(ResponseDataAPI
                                .successWithoutMeta(deviceTokenService.registerDevice(userPrincipal.getId(),
                                                request.getDeviceToken(), request.getPlatform())));
        }

        @PatchMapping("/{notificationId}/read")
        @ApiOperation("Mark notification as read")
        public ResponseEntity<ResponseDataAPI> markAsRead(
                        @PathVariable UUID notificationId) {
                return ResponseEntity.ok(ResponseDataAPI
                                .successWithoutMeta(notificationService.markAsRead(notificationId)));
        }

        // test notification endpoint for current user
        @PostMapping("/test")
        @ApiOperation("Send test notification to user")
        public ResponseEntity<ResponseDataAPI> sendTestNotification(
                        @CurrentUser UserPrincipal userPrincipal) {
                notificationService.sendNotificationToUsers(
                                java.util.List.of(userPrincipal.getId()),
                                NotificationType.SYSTEM_ALERT,
                                "Test Notification:" + Date.from(Instant.now()),
                                "This is a test notification",
                                java.util.Map.of("key", "value"));
                return ResponseEntity.ok(ResponseDataAPI.successWithoutMeta("Test notification sent"));
        }

        @GetMapping("/unread-count")
        @ApiOperation("Get count of unread notifications")
        public ResponseEntity<ResponseDataAPI> getUnreadCount(
                        @CurrentUser UserPrincipal userPrincipal) {
                long count = notificationService.countUnreadNotifications(userPrincipal.getId());
                return ResponseEntity.ok(ResponseDataAPI.successWithoutMeta(count));
        }

        @DeleteMapping("/{notificationId}")
        @ApiOperation("Delete a notification")
        public ResponseEntity<ResponseDataAPI> deleteNotification(
                        @PathVariable UUID notificationId) {
                notificationService.deleteNotification(notificationId);
                return ResponseEntity.ok(ResponseDataAPI.successWithoutMeta(
                                "Notification with ID " + notificationId + " deleted successfully"));
        }

        /**
         * Internal endpoint for payment notifications from commerce-service
         * This endpoint does not require authentication as it's called internally
         * between services
         */
        @PostMapping("/internal/payment")
        @ApiOperation("Send payment notification (internal service endpoint)")
        public ResponseEntity<ResponseDataAPI> sendPaymentNotification(
                        @RequestBody PaymentNotificationRequest request) {
                try {
                        // log.info("Received payment notification request: userId={}, type={},
                        // orderCode={}",
                        // request.getUserId(), request.getType(), request.getOrderCode());
                        Map<String, String> metadata = Map.of();
                        metadata.put("orderCode", request.getOrderCode() != null ? request.getOrderCode() : "");
                        metadata.put("paymentStatus",
                                        request.getPaymentStatus() != null ? request.getPaymentStatus() : "");

                        notificationService.sendNotificationToUsers(
                                        List.of(request.getUserId()),
                                        request.getType(),
                                        request.getTitle(),
                                        request.getMessage(),
                                        request.getMetadata());

                        log.info("Payment notification sent successfully to user: {}", request.getUserId());
                        return ResponseEntity.ok(
                                        ResponseDataAPI.successWithoutMeta("Payment notification sent successfully"));
                } catch (Exception e) {
                        log.error("Failed to send payment notification", e);
                        return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(ResponseDataAPI.error(
                                                        "Failed to send payment notification: " + e.getMessage()));
                }
        }
}