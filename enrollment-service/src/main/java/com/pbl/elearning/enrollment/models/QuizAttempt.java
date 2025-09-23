package com.pbl.elearning.enrollment.models;

import javax.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "quiz_attempts_responses")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class QuizAttempt {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    private UUID userId;

    @ManyToOne
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private QuizQuestionAnswer question;

    private UUID selectedAnswerId;
    private String answerText;
    private Boolean isCorrect;
    private Integer pointsEarned;
    private Integer attemptNumber;
    private OffsetDateTime startedAt;
    private OffsetDateTime completedAt;
    private Integer timeTakenMinutes;
}
