package com.pbl.elearning.enrollment.models;

import javax.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.*;

@Entity
@Table(name = "quizzes")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Quiz {
    @Id
    @GeneratedValue
    private UUID id;

    private UUID lectureId;

    private String title;
    private String description;
    private Integer timeLimitMinutes;
    private Integer passingScore;
    private Integer maxAttempts;
    private Boolean isActive;
    private OffsetDateTime createdAt;
}
