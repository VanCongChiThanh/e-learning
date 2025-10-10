package com.pbl.elearning.enrollment.services.Impl;

import com.pbl.elearning.enrollment.models.Quiz;
import com.pbl.elearning.enrollment.payload.request.QuizRequest;
import com.pbl.elearning.enrollment.repository.QuizRepository;
import com.pbl.elearning.enrollment.services.QuizService;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class QuizServiceImpl implements QuizService {
    private final QuizRepository quizRepository;

    public QuizServiceImpl(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }
    
    @Override
    public Quiz createQuiz(QuizRequest request) {
        Quiz quiz = new Quiz();
        quiz.setLectureId(request.getLectureId());
        quiz.setTitle(request.getTitle());
        quiz.setDescription(request.getDescription());
        quiz.setTimeLimitMinutes(request.getTimeLimitMinutes());
        quiz.setPassingScore(request.getPassingScore());
        quiz.setMaxAttempts(request.getMaxAttempts());
        quiz.setIsActive(true);
        quiz.setNumberQuestions(request.getNumberQuestions());
        quiz.setCreatedAt(OffsetDateTime.now());
        return quizRepository.save(quiz);
    }

    @Override
    public Quiz getQuizById(UUID id) {
        Quiz quiz = quizRepository.findById(id).orElse(null);
        if (quiz != null) {
            return quiz;
        }
        return null;
    }

    @Override
    public List<Quiz> getAllQuizzesBylectureId(UUID id) {
        List<Quiz> quizzes = quizRepository.findBylectureId(id);
        return quizzes;
    }

    @Override
    public Quiz updateQuiz(UUID id, QuizRequest request) {
        Quiz quiz = getQuizById(id);
        if (quiz != null) {
            quiz.setLectureId(request.getLectureId());
            quiz.setTitle(request.getTitle());
            quiz.setDescription(request.getDescription());
            quiz.setTimeLimitMinutes(request.getTimeLimitMinutes());
            quiz.setPassingScore(request.getPassingScore());
            quiz.setMaxAttempts(request.getMaxAttempts());
            quiz.setNumberQuestions(request.getNumberQuestions());
            return quizRepository.save(quiz);
        }
        return null;
    }

    @Override
    public void deleteQuiz(UUID id) {
        quizRepository.deleteById(id);
    }
}
