package com.pbl.elearning.notification.domain;

import com.pbl.elearning.common.domain.AbstractEntity;
import com.pbl.elearning.notification.domain.enums.NotificationType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Notification extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;  // người nhận

    @Enumerated(EnumType.STRING)
    private NotificationType type; // COURSE, LESSON, SYSTEM

    private String title;

    private String message;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private Map<String, String> metadata = new HashMap<>();

    @Column(name = "is_read")
    private Boolean isRead = false;

}