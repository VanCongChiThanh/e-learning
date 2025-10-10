package com.pbl.elearning.enrollment.models;

import javax.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "quiz_attempts")
public class QuizAttempt {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(nullable = false)
    private UUID userId;

    @ManyToOne
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private QuizQuestionAnswer question;

    @Column(length = 1, nullable = false)
    private String selectedOption;

    private Boolean isCorrect;

    private Integer pointsEarned;

    private Integer attemptNumber;

    private OffsetDateTime startedAt;
    private OffsetDateTime completedAt;

    private Integer timeTakenMinutes;
}
