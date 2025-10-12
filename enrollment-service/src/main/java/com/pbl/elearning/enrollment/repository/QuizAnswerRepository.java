package com.pbl.elearning.enrollment.repository;

import com.pbl.elearning.enrollment.models.QuizAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuizAnswerRepository extends JpaRepository<QuizAnswer, UUID> {
    
    List<QuizAnswer> findByQuizSubmissionId(UUID quizSubmissionId);
    
    List<QuizAnswer> findByQuizSubmissionIdAndIsCorrect(UUID quizSubmissionId, Boolean isCorrect);
}