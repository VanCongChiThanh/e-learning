package com.pbl.elearning.web.endpoint.enrollment;

import com.pbl.elearning.enrollment.payload.request.QuizQuestionAnswerRequest;
import com.pbl.elearning.enrollment.payload.response.QuizQuestionAnswerResponse;
import com.pbl.elearning.enrollment.services.QuizQuestionAnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/quiz-questions")
@RequiredArgsConstructor
public class QuizQuestionAnswerController {

    private final QuizQuestionAnswerService service;

    @PostMapping("/{id}")
    public ResponseEntity<QuizQuestionAnswerResponse> create(@PathVariable UUID id, @RequestBody QuizQuestionAnswerRequest request) {
        return ResponseEntity.ok(service.createQuizQuestionAnswer(id, request));
    }

    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<List<QuizQuestionAnswerResponse>> getByQuizId(@PathVariable UUID quizId) {
        return ResponseEntity.ok(service.getAllByQuizId(quizId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuizQuestionAnswerResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuizQuestionAnswerResponse> update(@PathVariable UUID id,
                                                             @RequestBody QuizQuestionAnswerRequest request) {
        return ResponseEntity.ok(service.updateQuizQuestionAnswer(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteQuizQuestionAnswer(id);
        return ResponseEntity.noContent().build();
    }
}