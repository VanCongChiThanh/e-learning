package com.pbl.elearning.enrollment.services.Impl;

import com.pbl.elearning.enrollment.models.*;
import com.pbl.elearning.enrollment.payload.request.QuizSubmissionRequest;
import com.pbl.elearning.enrollment.payload.response.QuizSubmissionResponse;
import com.pbl.elearning.enrollment.payload.response.QuizStatisticsResponse;
import com.pbl.elearning.enrollment.repository.*;
import com.pbl.elearning.enrollment.services.QuizSubmissionService;
import com.pbl.elearning.security.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizSubmissionServiceImpl implements QuizSubmissionService {

    private final QuizSubmissionRepository quizSubmissionRepository;
    private final QuizRepository quizRepository;
    private final QuizQuestionAnswerRepository quizQuestionAnswerRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    public QuizSubmissionResponse submitQuiz(QuizSubmissionRequest request) {
        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        Enrollment enrollment = enrollmentRepository.findById(request.getEnrollmentId())
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        User user = new User();
        user.setId(request.getUserId());

        if (!canUserAttemptQuiz(request.getQuizId(), request.getUserId())) {
            throw new RuntimeException("Maximum attempts reached");
        }

        Integer nextAttemptNumber = quizSubmissionRepository.countAttemptsByQuizAndUser(
                request.getQuizId(), request.getUserId()) + 1;

        QuizSubmission submission = QuizSubmission.builder()
                .quiz(quiz)
                .user(user) 
                .enrollment(enrollment)
                .attemptNumber(nextAttemptNumber)
                .startedAt(OffsetDateTime.now())
                .submittedAt(OffsetDateTime.now())
                .isCompleted(true)
                .build();

        List<QuizQuestionAnswer> allQuestions = quizQuestionAnswerRepository.findByQuiz_Id(request.getQuizId());
        
        int maxPossibleScore = allQuestions.stream()
                .mapToInt(QuizQuestionAnswer::getPoints)
                .sum();

        Map<UUID, QuizSubmissionRequest.QuizAnswerRequest> submittedAnswersMap = request.getAnswers().stream()
                .collect(Collectors.toMap(
                    QuizSubmissionRequest.QuizAnswerRequest::getQuestionId,
                    answer -> answer
                ));

        List<QuizAnswer> answers = new ArrayList<>();
        int totalScore = 0;

        for (QuizQuestionAnswer question : allQuestions) {
            QuizSubmissionRequest.QuizAnswerRequest submittedAnswer = submittedAnswersMap.get(question.getId());
            
            Integer selectedAnswerIndex = null;
            boolean isCorrect = false;
            int pointsEarned = 0;
            
            if (submittedAnswer != null) {
                selectedAnswerIndex = submittedAnswer.getSelectedAnswerIndex();
                isCorrect = question.getCorrectAnswerIndex().equals(selectedAnswerIndex);
                pointsEarned = isCorrect ? question.getPoints() : 0;
            }

            QuizAnswer answer = QuizAnswer.builder()
                    .quizSubmission(submission)
                    .question(question)
                    .selectedAnswerIndex(selectedAnswerIndex) 
                    .isCorrect(isCorrect) 
                    .pointsEarned(pointsEarned)
                    .build();

            answers.add(answer);
            totalScore += pointsEarned;
        }

        double scorePercentage = maxPossibleScore > 0 ? (double) totalScore / maxPossibleScore * 100 : 0;
        boolean isPassed = scorePercentage >= quiz.getPassingScore();

        submission.setAnswers(answers);
        submission.setTotalScore(totalScore);
        submission.setMaxPossibleScore(maxPossibleScore);
        submission.setScorePercentage(scorePercentage);
        submission.setIsPassed(isPassed);

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
                .startedAt(submission.getStartedAt())
                .submittedAt(submission.getSubmittedAt())
                .timeTakenMinutes(submission.getTimeTakenMinutes())
                .isCompleted(submission.getIsCompleted())
                .answers(answerResponses)
                .build();
    }
}