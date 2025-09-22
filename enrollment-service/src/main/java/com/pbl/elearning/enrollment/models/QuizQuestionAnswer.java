package com.pbl.elearning.enrollment.models;

import javax.persistence.*;

import com.pbl.elearning.enrollment.Enum.QuestionType;
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

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    private String questionText;

    @Enumerated(EnumType.STRING)
    private QuestionType questionType;

    private String answerText;
    private Boolean isCorrect;
    private Integer points;
    private Integer sortOrder;
    private OffsetDateTime createdAt;
}
