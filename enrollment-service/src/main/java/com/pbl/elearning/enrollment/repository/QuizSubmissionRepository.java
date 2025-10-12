package com.pbl.elearning.enrollment.repository;

import com.pbl.elearning.enrollment.models.QuizSubmission;
import com.pbl.elearning.enrollment.models.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, UUID> {
    
    List<QuizSubmission> findByQuizIdAndUserIdOrderByAttemptNumberDesc(UUID quizId, UUID userId);
    
    Optional<QuizSubmission> findByQuizIdAndUserIdAndAttemptNumber(UUID quizId, UUID userId, Integer attemptNumber);
    
    @Query("SELECT COUNT(qs) FROM QuizSubmission qs WHERE qs.quiz.id = :quizId AND qs.user.id = :userId")
    Integer countAttemptsByQuizAndUser(@Param("quizId") UUID quizId, @Param("userId") UUID userId);
    
    @Query("SELECT qs FROM QuizSubmission qs WHERE qs.quiz.id = :quizId AND qs.user.id = :userId ORDER BY qs.attemptNumber DESC")
    Optional<QuizSubmission> findLatestAttemptByQuizAndUser(@Param("quizId") UUID quizId, @Param("userId") UUID userId);
    
    List<QuizSubmission> findByEnrollmentId(UUID enrollmentId);
    List<QuizSubmission> findByEnrollment(Enrollment enrollment);
    
    @Query("SELECT qs FROM QuizSubmission qs WHERE qs.enrollment.id = :enrollmentId AND qs.isPassed = true")
    List<QuizSubmission> findPassedSubmissionsByEnrollment(@Param("enrollmentId") UUID enrollmentId);
}