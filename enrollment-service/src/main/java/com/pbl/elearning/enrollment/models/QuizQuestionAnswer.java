package com.pbl.elearning.enrollment.models;

import javax.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "quiz_questions_answers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestionAnswer {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(nullable = false)
    private String questionText;

    @Column(nullable = false)
    private String optionA;

    @Column(nullable = false)
    private String optionB;

    @Column(nullable = false)
    private String optionC;

    @Column(nullable = false)
    private String optionD;

    @Column(nullable = false, length = 1)
    private String correctAnswer;

    private Integer points;

    private Integer sortOrder;

    private OffsetDateTime createdAt;
}
