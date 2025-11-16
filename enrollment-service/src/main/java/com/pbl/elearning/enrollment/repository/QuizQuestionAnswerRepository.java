package com.pbl.elearning.enrollment.repository;

import com.pbl.elearning.enrollment.models.QuizQuestionAnswer;
import com.pbl.elearning.enrollment.models.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuizQuestionAnswerRepository extends JpaRepository<QuizQuestionAnswer, UUID> {
    List<QuizQuestionAnswer> findByQuiz(Quiz quiz);
    List<QuizQuestionAnswer> findByQuiz_Id(UUID quizId);
    List<QuizQuestionAnswer> findAllByQuizId(UUID quizId);
}