package com.pbl.elearning.enrollment.services.Impl;

import com.pbl.elearning.enrollment.models.*;
import com.pbl.elearning.enrollment.payload.request.QuizSubmissionRequest;
import com.pbl.elearning.enrollment.payload.request.QuizSubmissionRequest.QuizAnswerRequest;
import com.pbl.elearning.enrollment.payload.response.QuizSubmissionResponse;
import com.pbl.elearning.enrollment.payload.response.QuizStatisticsResponse;
import com.pbl.elearning.enrollment.repository.*;
import com.pbl.elearning.enrollment.services.QuizSubmissionService;
import com.pbl.elearning.security.domain.User;
import com.pbl.elearning.security.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizSubmissionServiceImpl implements QuizSubmissionService {

    private final QuizSubmissionRepository quizSubmissionRepository;
    private final QuizRepository quizRepository;
    private final QuizQuestionAnswerRepository quizQuestionAnswerRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
        
    @Override
    public QuizSubmissionResponse submitQuiz(QuizSubmissionRequest dto, UUID userId) {

        Quiz quiz = quizRepository.findById(dto.getQuizId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Quiz: " + dto.getQuizId()));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy User: " + userId));

        Enrollment enrollment = enrollmentRepository.findById(dto.getEnrollmentId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy Enrollment: " + dto.getEnrollmentId()));

        int attempts = quizSubmissionRepository.countByQuizAndUser(quiz, user);
        if (quiz.getMaxAttempts() != null && attempts >= quiz.getMaxAttempts()) {
                throw new IllegalStateException("Bạn đã hết số lần làm bài cho quiz này.");
        }
        
        QuizSubmission submission = new QuizSubmission();
        submission.setQuiz(quiz);
        submission.setUser(user);
        submission.setEnrollment(enrollment);
        submission.setAttemptNumber(attempts + 1);
        submission.setStartedAt(dto.getStartedAt());
        submission.setSubmittedAt(OffsetDateTime.now());
        
        if (dto.getStartedAt() != null) {
            long timeTaken = ChronoUnit.MINUTES.between(dto.getStartedAt(), submission.getSubmittedAt());
            submission.setTimeTakenMinutes((int) timeTaken);
        }

        Map<UUID, QuizQuestionAnswer> questionMap = quizQuestionAnswerRepository.findByQuiz(quiz)
                .stream()
                .collect(Collectors.toMap(QuizQuestionAnswer::getId, q -> q));

        int totalScore = 0;
        int maxPossibleScore = 0;
        List<QuizAnswer> studentAnswers = new ArrayList<>();

        for (QuizAnswerRequest ansDto : dto.getAnswers()) {
            QuizQuestionAnswer question = questionMap.get(ansDto.getQuestionId());
            if (question == null) continue;

            maxPossibleScore += question.getPoints(); 

            boolean isCorrect = false;
            int pointsEarned = 0;

            if (ansDto.getSelectedAnswerIndex() != null && 
                question.getCorrectAnswerIndex().equals(ansDto.getSelectedAnswerIndex())) 
            {
                isCorrect = true;
                pointsEarned = question.getPoints();
                totalScore += pointsEarned;
            }

            QuizAnswer studentAnswer = new QuizAnswer();
            studentAnswer.setQuizSubmission(submission); 
            studentAnswer.setQuestion(question);
            studentAnswer.setSelectedAnswerIndex(ansDto.getSelectedAnswerIndex());
            studentAnswer.setIsCorrect(isCorrect);
            studentAnswer.setPointsEarned(pointsEarned);
            
            studentAnswers.add(studentAnswer);
        }

        submission.setTotalScore(totalScore);
        submission.setMaxPossibleScore(maxPossibleScore);
        submission.setScorePercentage(maxPossibleScore > 0 ? ((double) totalScore / maxPossibleScore) * 100 : 0);
        submission.setIsPassed(totalScore >= quiz.getPassingScore());
        submission.setIsCompleted(true);
        submission.setAnswers(studentAnswers); 
        QuizSubmission savedSubmission = quizSubmissionRepository.save(submission);
        
        return convertToResponse(savedSubmission);

    }

    @Override
    public List<QuizSubmissionResponse> getQuizAttempts(UUID quizId, UUID userId) {
        List<QuizSubmission> submissions = quizSubmissionRepository.findByQuizIdAndUserIdOrderByAttemptNumberDesc(quizId, userId);
        return submissions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public QuizSubmissionResponse getLatestAttempt(UUID quizId, UUID userId) {
        return quizSubmissionRepository.findLatestAttemptByQuizAndUser(quizId, userId)
                .map(this::convertToResponse)
                .orElse(null);
    }

    @Override
    public QuizSubmissionResponse getQuizSubmission(UUID submissionId) {
        QuizSubmission submission = quizSubmissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Quiz submission not found"));
        return convertToResponse(submission);
    }

    @Override
    public Boolean canUserAttemptQuiz(UUID quizId, UUID userId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        Integer currentAttempts = quizSubmissionRepository.countAttemptsByQuizAndUser(quizId, userId);
        return quiz.getMaxAttempts() == null || currentAttempts < quiz.getMaxAttempts();
    }

    @Override
    public List<QuizSubmissionResponse> getQuizSubmissionsByEnrollment(UUID enrollmentId) {
        List<QuizSubmission> submissions = quizSubmissionRepository.findByEnrollmentId(enrollmentId);
        return submissions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public QuizStatisticsResponse getQuizStatistics(UUID userId, UUID courseId) {
        return QuizStatisticsResponse.builder()
                .userId(userId)
                .courseId(courseId)
                .build();
    }

    private QuizSubmissionResponse convertToResponse(QuizSubmission submission) {
        List<QuizSubmissionResponse.QuizAnswerResponse> answerResponses = submission.getAnswers().stream()
                .map(answer -> QuizSubmissionResponse.QuizAnswerResponse.builder()
                        .questionId(answer.getQuestion().getId())
                        .questionText(answer.getQuestion().getQuestionText())
                        .options(answer.getQuestion().getOptions())
                        .selectedAnswerIndex(answer.getSelectedAnswerIndex())
                        .correctAnswerIndex(answer.getQuestion().getCorrectAnswerIndex())
                        .isCorrect(answer.getIsCorrect())
                        .pointsEarned(answer.getPointsEarned())
                        .maxPoints(answer.getQuestion().getPoints())
                        .build())
                .collect(Collectors.toList());

        return QuizSubmissionResponse.builder()
                .id(submission.getId())
                .quizId(submission.getQuiz().getId())
                .quizTitle(submission.getQuiz().getTitle())
                .userId(submission.getUser().getId())
                .userEmail(submission.getUser().getEmail())
                .attemptNumber(submission.getAttemptNumber())
                .totalScore(submission.getTotalScore())
                .maxPossibleScore(submission.getMaxPossibleScore())
                .scorePercentage(submission.getScorePercentage())
                .isPassed(submission.getIsPassed())
                .submittedAt(submission.getSubmittedAt())
                .timeTakenMinutes(submission.getTimeTakenMinutes())
                .isCompleted(submission.getIsCompleted())
                .answers(answerResponses)
                .build();
    }
}