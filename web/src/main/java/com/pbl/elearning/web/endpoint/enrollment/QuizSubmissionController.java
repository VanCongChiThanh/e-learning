package com.pbl.elearning.web.endpoint.enrollment;

import com.pbl.elearning.enrollment.payload.request.QuizSubmissionRequest;
import com.pbl.elearning.enrollment.payload.response.QuizStatisticsResponse;
import com.pbl.elearning.enrollment.payload.response.QuizSubmissionResponse;
import com.pbl.elearning.enrollment.services.QuizSubmissionService;
import com.pbl.elearning.security.annotation.CurrentUser;
import com.pbl.elearning.security.domain.UserPrincipal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/quiz-submissions")
@RequiredArgsConstructor
public class QuizSubmissionController {

    private final QuizSubmissionService quizSubmissionService;

    @PostMapping("/submit")
    public ResponseEntity<QuizSubmissionResponse> submitQuiz(@RequestBody QuizSubmissionRequest request, @CurrentUser UserPrincipal userPrincipal) {
        UUID currentUserId = userPrincipal.getId();
        try {
            QuizSubmissionResponse result = quizSubmissionService.submitQuiz(request, currentUserId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        } 
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuizSubmissionResponse> getQuizSubmissionById(@PathVariable UUID id) {
        QuizSubmissionResponse response = quizSubmissionService.getQuizSubmission(id);
        return response != null ? ResponseEntity.ok(response) : ResponseEntity.notFound().build();
    }

    @GetMapping("/quiz/{quizId}/user/{userId}")
    public ResponseEntity<List<QuizSubmissionResponse>> getQuizAttempts(
            @PathVariable UUID quizId, 
            @PathVariable UUID userId) {
        List<QuizSubmissionResponse> responses = quizSubmissionService.getQuizAttempts(quizId, userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/quiz/{quizId}/user/{userId}/latest")
    public ResponseEntity<QuizSubmissionResponse> getLatestAttempt(
            @PathVariable UUID quizId, 
            @PathVariable UUID userId) {
        QuizSubmissionResponse response = quizSubmissionService.getLatestAttempt(quizId, userId);
        return response != null ? ResponseEntity.ok(response) : ResponseEntity.notFound().build();
    }

    @GetMapping("/enrollment/{enrollmentId}")
    public ResponseEntity<List<QuizSubmissionResponse>> getQuizSubmissionsByEnrollment(@PathVariable UUID enrollmentId) {
        List<QuizSubmissionResponse> responses = quizSubmissionService.getQuizSubmissionsByEnrollment(enrollmentId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/quiz/{quizId}/user/{userId}/can-attempt")
    public ResponseEntity<Boolean> canUserAttemptQuiz(
            @PathVariable UUID quizId, 
            @PathVariable UUID userId) {
        Boolean canAttempt = quizSubmissionService.canUserAttemptQuiz(quizId, userId);
        return ResponseEntity.ok(canAttempt);
    }

    @GetMapping("/statistics/user/{userId}/course/{courseId}")
    public ResponseEntity<QuizStatisticsResponse> getQuizStatistics(
            @PathVariable UUID userId, 
            @PathVariable UUID courseId) {
        QuizStatisticsResponse statistics = quizSubmissionService.getQuizStatistics(userId, courseId);
        return ResponseEntity.ok(statistics);
    }
}