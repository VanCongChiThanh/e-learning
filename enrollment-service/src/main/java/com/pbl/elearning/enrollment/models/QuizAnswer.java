package com.pbl.elearning.enrollment.models;

import javax.persistence.*;
import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "quizAnswers")
public class QuizAnswer {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quizSubmissionId", nullable = false)
    private QuizSubmission quizSubmission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questionId", nullable = false)
    private QuizQuestionAnswer question;

    @Column(nullable = true) 
    private Integer selectedAnswerIndex; 

    @Builder.Default
    private Boolean isCorrect = false;

    private Integer pointsEarned;
}