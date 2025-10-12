package com.pbl.elearning.enrollment.models;

import javax.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "quizQuestionsAnswers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestionAnswer {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quizId", nullable = false)
    private Quiz quiz;

    @Column(nullable = false)
    private String questionText;

    @ElementCollection
    @CollectionTable(name = "quizQuestionOptions", joinColumns = @JoinColumn(name = "questionId"))
    @Column(name = "optionText", nullable = false)
    @OrderColumn(name = "optionOrder")
    private List<String> options;

    @Column(nullable = false)
    private Integer correctAnswerIndex; 

    private Integer points;

    private Integer sortOrder;

    private OffsetDateTime createdAt;
}
