package com.pbl.elearning.notification.domain;

import com.pbl.elearning.common.domain.AbstractEntity;
import com.pbl.elearning.notification.domain.enums.NotificationType;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    private UUID userId;  // người nhận

    @Enumerated(EnumType.STRING)
    private NotificationType type; // COURSE, LESSON, SYSTEM

    private String title;

    private String message;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private Map<String, String> metadata; // params đi kèm

    private Boolean isRead = false;

}