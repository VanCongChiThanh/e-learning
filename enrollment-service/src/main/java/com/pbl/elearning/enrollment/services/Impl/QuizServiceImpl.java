package com.pbl.elearning.enrollment.services.Impl;

import com.pbl.elearning.enrollment.models.Quiz;
import com.pbl.elearning.enrollment.payload.request.QuizRequest;
import com.pbl.elearning.enrollment.payload.response.QuizResponse;
import com.pbl.elearning.enrollment.repository.QuizRepository;
import com.pbl.elearning.enrollment.services.QuizService;
import com.pbl.elearning.course.domain.Lecture;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class QuizServiceImpl implements QuizService {
    private final QuizRepository quizRepository;

    public QuizServiceImpl(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }
    
    private QuizResponse mapToResponse(Quiz quiz) {
        return QuizResponse.builder()
                .id(quiz.getId())
                .lectureId(quiz.getLecture() != null ? quiz.getLecture().getLectureId() : null)
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .timeLimitMinutes(quiz.getTimeLimitMinutes())
                .passingScore(quiz.getPassingScore())
                .maxAttempts(quiz.getMaxAttempts())
                .numberQuestions(quiz.getNumberQuestions())
                .isActive(quiz.getIsActive())
                .createdAt(quiz.getCreatedAt())
                .build();
    }
    
    @Override
    public Quiz createQuiz(QuizRequest request) {
        Lecture lecture = Lecture.builder()
                .lectureId(request.getLectureId())
                .build();
        
        Quiz quiz = new Quiz();
        quiz.setLecture(lecture);
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
        List<Quiz> quizzes = quizRepository.findByLecture_LectureId(id);
        return quizzes;
    }

    @Override
    public Quiz updateQuiz(UUID id, QuizRequest request) {
        Quiz quiz = getQuizById(id);
        if (quiz != null) {
            Lecture lecture = Lecture.builder()
                    .lectureId(request.getLectureId())
                    .build();
            
            quiz.setLecture(lecture);
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
    
    @Override
    public QuizResponse getQuizResponseById(UUID id) {
        Quiz quiz = quizRepository.findById(id).orElse(null);
        return quiz != null ? mapToResponse(quiz) : null;
    }
    
    
    @Override
    public List<QuizResponse> getQuizResponsesByLectureId(UUID lectureId) {
        List<Quiz> quizzes = quizRepository.findByLecture_LectureId(lectureId);
        return quizzes.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
}
