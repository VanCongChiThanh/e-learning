package com.pbl.elearning.enrollment.services.Impl;

import com.pbl.elearning.enrollment.models.Quiz;
import com.pbl.elearning.enrollment.models.QuizQuestionAnswer;
import com.pbl.elearning.enrollment.payload.request.QuizQuestionAnswerRequest;
import com.pbl.elearning.enrollment.payload.response.QuizQuestionAnswerResponse;
import com.pbl.elearning.enrollment.repository.QuizQuestionAnswerRepository;
import com.pbl.elearning.enrollment.services.QuizQuestionAnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizQuestionAnswerServiceImpl implements QuizQuestionAnswerService {

    private final QuizQuestionAnswerRepository repository;

    private QuizQuestionAnswerResponse mapToResponse(QuizQuestionAnswer entity) {
        return QuizQuestionAnswerResponse.builder()
                .id(entity.getId())
                .quizId(entity.getQuiz().getId())
                .questionText(entity.getQuestionText())
                .questionType(entity.getQuestionType())
                .answerText(entity.getAnswerText())
                .isCorrect(entity.getIsCorrect())
                .points(entity.getPoints())
                .sortOrder(entity.getSortOrder())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    @Override
    public QuizQuestionAnswerResponse createQuizQuestionAnswer(UUID id, QuizQuestionAnswerRequest request) {
        Quiz quiz = Quiz.builder().id(id).build();
        QuizQuestionAnswer entity = QuizQuestionAnswer.builder()
                .quiz(quiz)
                .questionText(request.getQuestionText())
                .questionType(request.getQuestionType())
                .answerText(request.getAnswerText())
                .isCorrect(request.getIsCorrect())
                .points(request.getPoints())
                .sortOrder(request.getSortOrder())
                .build();
        QuizQuestionAnswer saved = repository.save(entity);
        return mapToResponse(saved);
    }

    @Override
    public List<QuizQuestionAnswerResponse> getAllByQuizId(UUID quizId) {
        return repository.findByQuizId(quizId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public QuizQuestionAnswerResponse getById(UUID id) {
        return repository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("QuizQuestionAnswer not found with id: " + id));
    }

    @Override
    public QuizQuestionAnswerResponse updateQuizQuestionAnswer(UUID id, QuizQuestionAnswerRequest request) {
        QuizQuestionAnswer entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("QuizQuestionAnswer not found with id: " + id));

        entity.setQuestionText(request.getQuestionText());
        entity.setQuestionType(request.getQuestionType());
        entity.setAnswerText(request.getAnswerText());
        entity.setIsCorrect(request.getIsCorrect());
        entity.setPoints(request.getPoints());
        entity.setSortOrder(request.getSortOrder());

        QuizQuestionAnswer updated = repository.save(entity);
        return mapToResponse(updated);
    }

    @Override
    public void deleteQuizQuestionAnswer(UUID id) {
        repository.deleteById(id);
    }
}