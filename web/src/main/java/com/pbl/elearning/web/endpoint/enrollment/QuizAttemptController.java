package com.pbl.elearning.web.endpoint.enrollment;

import com.pbl.elearning.enrollment.payload.request.QuizAttemptRequest;
import com.pbl.elearning.enrollment.payload.response.QuizAttemptResponse;
import com.pbl.elearning.enrollment.services.QuizAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/quiz-attempts")
@RequiredArgsConstructor
public class QuizAttemptController {

    private final QuizAttemptService service;

    @PostMapping
    public ResponseEntity<QuizAttemptResponse> create(@RequestBody QuizAttemptRequest request) {
        return ResponseEntity.ok(service.createQuizAttempt(request));
    }

    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<List<QuizAttemptResponse>> getByQuiz(@PathVariable UUID quizId) {
        return ResponseEntity.ok(service.getByQuizId(quizId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<QuizAttemptResponse>> getByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(service.getByUserId(userId));
    }
}