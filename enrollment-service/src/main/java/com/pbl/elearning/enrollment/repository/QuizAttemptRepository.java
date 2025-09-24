package com.pbl.elearning.enrollment.repository;

import com.pbl.elearning.enrollment.models.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, UUID> {
    List<QuizAttempt> findByQuizId(UUID quizId);
    List<QuizAttempt> findByUserId(UUID userId);
}