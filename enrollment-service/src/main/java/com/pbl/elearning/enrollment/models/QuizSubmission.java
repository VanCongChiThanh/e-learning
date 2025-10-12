package com.pbl.elearning.enrollment.models;

import com.pbl.elearning.security.domain.User;
import javax.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "quizSubmissions")
public class QuizSubmission {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quizId", nullable = false)
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollmentId", nullable = false)
    private Enrollment enrollment;

    @OneToMany(mappedBy = "quizSubmission", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<QuizAnswer> answers;

    private Integer attemptNumber;
    
    private Integer totalScore;
    private Integer maxPossibleScore;
    private Double scorePercentage;
    
    private Boolean isPassed; 
    
    private OffsetDateTime startedAt;
    private OffsetDateTime submittedAt;
    
    private Integer timeTakenMinutes;
    
    @Builder.Default
    private Boolean isCompleted = false;
}