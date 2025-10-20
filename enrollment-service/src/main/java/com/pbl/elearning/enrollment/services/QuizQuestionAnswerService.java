package com.pbl.elearning.enrollment.services;

import com.pbl.elearning.enrollment.payload.request.QuizQuestionAnswerRequest;
import com.pbl.elearning.enrollment.payload.request.BulkQuizQuestionsRequest;
import com.pbl.elearning.enrollment.payload.response.QuizQuestionAnswerResponse;

import java.util.List;
import java.util.UUID;

public interface QuizQuestionAnswerService {
    QuizQuestionAnswerResponse createQuizQuestionAnswer(UUID quiz, QuizQuestionAnswerRequest request);

    // Thêm method mới để tạo nhiều câu hỏi cùng lúc
    List<QuizQuestionAnswerResponse> createBulkQuizQuestions(UUID quizId, BulkQuizQuestionsRequest request);

    List<QuizQuestionAnswerResponse> getAllByQuizId(UUID quizId);

    QuizQuestionAnswerResponse getById(UUID id);

    QuizQuestionAnswerResponse updateQuizQuestionAnswer(UUID id, QuizQuestionAnswerRequest request);

    void deleteQuizQuestionAnswer(UUID id);
}