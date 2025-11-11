package com.pbl.elearning.notification.payload.request;

import com.pbl.elearning.notification.domain.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentNotificationRequest {
    private UUID userId;
    private NotificationType type;
    private String title;
    private String message;
    private Map<String, String> metadata;
    private String orderCode;
    private String paymentStatus;
}
