package com.pbl.elearning.enrollment.services.Impl;

import com.pbl.elearning.enrollment.models.Quiz;
import com.pbl.elearning.enrollment.models.QuizQuestionAnswer;
import com.pbl.elearning.enrollment.payload.request.QuestionCreateRequest;
import com.pbl.elearning.enrollment.payload.request.QuizCreateRequestDTO;
import com.pbl.elearning.enrollment.payload.response.QuizResponse;
import com.pbl.elearning.enrollment.repository.QuizQuestionAnswerRepository;
import com.pbl.elearning.enrollment.repository.QuizRepository;
import com.pbl.elearning.enrollment.services.QuizService;
import com.pbl.elearning.course.domain.Lecture;
import com.pbl.elearning.course.repository.LectureRepository;

import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

@Service
public class QuizServiceImpl implements QuizService {
    private final QuizRepository quizRepository;
    private final LectureRepository lectureRepository;
    private final QuizQuestionAnswerRepository questionRepository;
    public QuizServiceImpl(QuizRepository quizRepository, LectureRepository lectureRepository, QuizQuestionAnswerRepository questionRepository) {
        this.quizRepository = quizRepository;
        this.lectureRepository = lectureRepository;
        this.questionRepository = questionRepository;
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
    @Transactional 
    public Quiz createQuiz(QuizCreateRequestDTO dto) {
        Lecture lecture = lectureRepository.findById(dto.getLectureId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Lecture với ID: " + dto.getLectureId()));

        Quiz quiz = new Quiz();
        quiz.setLecture(lecture);
        quiz.setTitle(dto.getTitle());
        quiz.setDescription(dto.getDescription());
        quiz.setTimeLimitMinutes(dto.getTimeLimitMinutes());
        quiz.setPassingScore(dto.getPassingScore());
        quiz.setMaxAttempts(dto.getMaxAttempts());
        quiz.setCreatedAt(OffsetDateTime.now());
        
        quiz.setNumberQuestions(dto.getQuestions().size());

        Quiz savedQuiz = quizRepository.save(quiz);

        List<QuizQuestionAnswer> questionEntities = new ArrayList<>();
        for (QuestionCreateRequest qDto : dto.getQuestions()) {
            QuizQuestionAnswer question = new QuizQuestionAnswer();
            question.setQuiz(savedQuiz); 
            question.setQuestionText(qDto.getQuestionText());
            question.setOptions(qDto.getOptions());
            question.setCorrectAnswerIndex(qDto.getCorrectAnswerIndex());
            question.setPoints(qDto.getPoints());
            question.setSortOrder(qDto.getSortOrder());
            question.setCreatedAt(OffsetDateTime.now());
            
            questionEntities.add(question);
        }

        questionRepository.saveAll(questionEntities);
        return savedQuiz;
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
    public Quiz updateQuiz(UUID id, QuizCreateRequestDTO request) {
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
            quiz.setNumberQuestions(request.getQuestions().size());
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
