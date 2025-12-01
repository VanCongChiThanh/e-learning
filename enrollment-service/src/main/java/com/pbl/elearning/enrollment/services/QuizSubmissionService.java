package com.pbl.elearning.enrollment.services;

import com.pbl.elearning.enrollment.payload.request.QuizSubmissionRequest;
import com.pbl.elearning.enrollment.payload.response.QuizStatisticsResponse;
import com.pbl.elearning.enrollment.payload.response.QuizSubmissionResponse;
import com.pbl.elearning.security.domain.UserPrincipal;

import java.util.List;
import java.util.UUID;

public interface QuizSubmissionService {
    
    /**
     * Submit quiz answers and calculate score
     */
    QuizSubmissionResponse submitQuiz(QuizSubmissionRequest request, UserPrincipal userPrincipal);
    
    /**
     * Get all attempts for a quiz by user
     */
    List<QuizSubmissionResponse> getQuizAttempts(UUID quizId, UUID userId);
    
    /**
     * Get latest attempt for a quiz by user
     */
    QuizSubmissionResponse getLatestAttempt(UUID quizId, UUID userId);
    
    /**
     * Get quiz submission by id
     */
    QuizSubmissionResponse getQuizSubmission(UUID submissionId);
    
    /**
     * Check if user can attempt quiz (based on max attempts)
     */
    Boolean canUserAttemptQuiz(UUID quizId, UUID userId);
    
    /**
     * Get all quiz submissions for an enrollment
     */
    List<QuizSubmissionResponse> getQuizSubmissionsByEnrollment(UUID enrollmentId);
    
    /**
     * Get quiz statistics for user
     */
    QuizStatisticsResponse getQuizStatistics(UUID userId, UUID courseId);
}