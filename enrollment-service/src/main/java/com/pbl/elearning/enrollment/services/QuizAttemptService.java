package com.pbl.elearning.enrollment.services;

import com.pbl.elearning.enrollment.payload.request.QuizAttemptRequest;
import com.pbl.elearning.enrollment.payload.response.QuizAttemptResponse;

import java.util.List;
import java.util.UUID;

public interface QuizAttemptService {
    QuizAttemptResponse createQuizAttempt(QuizAttemptRequest request);
    List<QuizAttemptResponse> getByQuizId(UUID quizId);
    List<QuizAttemptResponse> getByUserId(UUID userId);
}