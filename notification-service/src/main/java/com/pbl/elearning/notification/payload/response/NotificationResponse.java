package com.pbl.elearning.notification.payload.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.pbl.elearning.notification.domain.Notification;
import com.pbl.elearning.notification.domain.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder
public class NotificationResponse {

    private UUID id;

    private UUID userId;

    private NotificationType type;

    private String title;

    private String message;

    private String metadata;

    private Boolean isRead;

    private Timestamp createdAt;
    public static NotificationResponse toResponse(Notification notification) {
        if (notification == null) return null;

        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .metadata(notification.getMetadata() != null ? notification.getMetadata().toString() : null)
                .isRead(Boolean.TRUE.equals(notification.getIsRead()))
                .createdAt(notification.getCreatedAt())
                .build();
    }


}