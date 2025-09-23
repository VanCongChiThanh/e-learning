package com.pbl.elearning.enrollment.services.Impl;

import com.pbl.elearning.enrollment.models.*;
import com.pbl.elearning.enrollment.payload.request.QuizAttemptRequest;
import com.pbl.elearning.enrollment.payload.response.QuizAttemptResponse;
import com.pbl.elearning.enrollment.repository.*;
import com.pbl.elearning.enrollment.services.QuizAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizAttemptServiceImpl implements QuizAttemptService {

    private final QuizAttemptRepository repository;
    private final QuizRepository quizRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final QuizQuestionAnswerRepository questionRepository;

    private QuizAttemptResponse mapToResponse(QuizAttempt attempt) {
        return QuizAttemptResponse.builder()
                .id(attempt.getId())
                .quizId(attempt.getQuiz().getId())
                .userId(attempt.getUserId())
                .enrollmentId(attempt.getEnrollment().getId())
                .questionId(attempt.getQuestion().getId())
                .selectedAnswerId(attempt.getSelectedAnswerId())
                .answerText(attempt.getAnswerText())
                .isCorrect(attempt.getIsCorrect())
                .pointsEarned(attempt.getPointsEarned())
                .attemptNumber(attempt.getAttemptNumber())
                .startedAt(attempt.getStartedAt())
                .completedAt(attempt.getCompletedAt())
                .timeTakenMinutes(attempt.getTimeTakenMinutes())
                .build();
    }

    @Override
    public QuizAttemptResponse createQuizAttempt(QuizAttemptRequest request) {
        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        Enrollment enrollment = enrollmentRepository.findById(request.getEnrollmentId())
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        QuizQuestionAnswer question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found"));

        QuizAttempt attempt = QuizAttempt.builder()
                .quiz(quiz)
                .userId(request.getUserId())
                .enrollment(enrollment)
                .question(question)
                .selectedAnswerId(request.getSelectedAnswerId())
                .answerText(request.getAnswerText())
                .isCorrect(request.getIsCorrect())
                .pointsEarned(request.getPointsEarned())
                .attemptNumber(request.getAttemptNumber())
                .timeTakenMinutes(request.getTimeTakenMinutes())
                .startedAt(OffsetDateTime.now())
                .build();

        QuizAttempt saved = repository.save(attempt);
        return mapToResponse(saved);
    }

    @Override
    public List<QuizAttemptResponse> getByQuizId(UUID quizId) {
        return repository.findByQuizId(quizId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuizAttemptResponse> getByUserId(UUID userId) {
        return repository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
}