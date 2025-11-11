package com.pbl.elearning.enrollment.services;

import com.pbl.elearning.enrollment.payload.request.QuestionCreateRequest;
import com.pbl.elearning.enrollment.payload.response.QuizQuestionAnswerResponse;

import java.util.List;
import java.util.UUID;

public interface QuizQuestionAnswerService {
    List<QuizQuestionAnswerResponse> getAllByQuizId(UUID quizId);

    QuizQuestionAnswerResponse getById(UUID id);

    QuizQuestionAnswerResponse updateQuizQuestionAnswer(UUID id, QuestionCreateRequest request);

    void deleteQuizQuestionAnswer(UUID id);
}