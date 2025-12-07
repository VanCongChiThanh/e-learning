package com.pbl.elearning.enrollment.services.Impl;

import com.pbl.elearning.enrollment.models.QuizQuestionAnswer;
import com.pbl.elearning.enrollment.payload.request.QuestionCreateRequest;
import com.pbl.elearning.enrollment.payload.response.QuizQuestionAnswerResponse;
import com.pbl.elearning.enrollment.repository.QuizQuestionAnswerRepository;
import com.pbl.elearning.enrollment.services.QuizQuestionAnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizQuestionAnswerServiceImpl implements QuizQuestionAnswerService {

    private final QuizQuestionAnswerRepository repository;

    public static QuizQuestionAnswerResponse mapToResponse(QuizQuestionAnswer entity) {
        return mapToResponseWithShuffle(entity, false);
    }
    
    public static QuizQuestionAnswerResponse mapToResponseWithShuffle(QuizQuestionAnswer entity, boolean shouldShuffle) {
        List<String> options = new ArrayList<>(entity.getOptions());
        Integer correctAnswerIndex = entity.getCorrectAnswerIndex();
        
        if (shouldShuffle && options.size() > 1) {
            String correctAnswer = options.get(correctAnswerIndex);
            
            Collections.shuffle(options);
            
            correctAnswerIndex = options.indexOf(correctAnswer);
        }
        
        return QuizQuestionAnswerResponse.builder()
                .id(entity.getId())
                .questionText(entity.getQuestionText())
                .options(options)
                .correctAnswerIndex(correctAnswerIndex)
                .points(entity.getPoints())
                .sortOrder(entity.getSortOrder())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    @Override
    public QuizQuestionAnswerResponse getById(UUID id) {
        return repository.findById(id)
                .map(QuizQuestionAnswerServiceImpl::mapToResponse)
                .orElseThrow(() -> new RuntimeException("QuizQuestionAnswer not found with id: " + id));
    }

    @Override
    public QuizQuestionAnswerResponse updateQuizQuestionAnswer(UUID id, QuestionCreateRequest request) {
        QuizQuestionAnswer entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("QuizQuestionAnswer not found with id: " + id));

        entity.setQuestionText(request.getQuestionText());
        entity.setOptions(request.getOptions());
        entity.setCorrectAnswerIndex(request.getCorrectAnswerIndex());
        entity.setPoints(request.getPoints());
        entity.setSortOrder(request.getSortOrder());

        QuizQuestionAnswer updated = repository.save(entity);
        return mapToResponse(updated);
    }

    @Override
    public void deleteQuizQuestionAnswer(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public List<QuizQuestionAnswerResponse> getAllByQuizId(UUID quizId) {
        List<QuizQuestionAnswer> entities = repository.findAllByQuizId(quizId);

        List<QuizQuestionAnswer> shuffledQuestions = new ArrayList<>(entities);
        Collections.shuffle(shuffledQuestions);

        return shuffledQuestions.stream()
                .map(question -> mapToResponseWithShuffle(question, true))
                .collect(Collectors.toList());
    }
}
