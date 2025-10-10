package com.pbl.elearning.enrollment.models;

import javax.persistence.*;
import com.pbl.elearning.course.domain.Lecture;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lectureId", nullable = false)
    private Lecture lecture;

    private String title;
    private String description;
    private Integer timeLimitMinutes;
    private Integer passingScore;
    private Integer numberQuestions;
    private Integer maxAttempts;
    private Boolean isActive;
    private OffsetDateTime createdAt;
}
