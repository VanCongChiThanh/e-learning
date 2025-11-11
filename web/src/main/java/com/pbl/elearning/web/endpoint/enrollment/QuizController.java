package com.pbl.elearning.web.endpoint.enrollment;

import com.pbl.elearning.enrollment.models.Quiz;
import com.pbl.elearning.enrollment.payload.request.QuizCreateRequestDTO;
import com.pbl.elearning.enrollment.payload.response.QuizResponse;
import com.pbl.elearning.enrollment.services.QuizService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

@RestController
@RequestMapping("/v1/quizzes")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    private QuizResponse toResponse(Quiz quiz) {
        return QuizResponse.builder()
                .id(quiz.getId())
                .lectureId(quiz.getLecture() != null ? quiz.getLecture().getLectureId() : null)
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .timeLimitMinutes(quiz.getTimeLimitMinutes())
                .passingScore(quiz.getPassingScore())
                .maxAttempts(quiz.getMaxAttempts())
                .isActive(quiz.getIsActive())
                .numberQuestions(quiz.getNumberQuestions())
                .createdAt(quiz.getCreatedAt())
                .build();
    }

    @GetMapping("/{id}")
    
    public ResponseEntity<QuizResponse> getQuizById(@PathVariable UUID id) {
        Quiz quiz = quizService.getQuizById(id);
        if (quiz != null) {
            return ResponseEntity.ok(toResponse(quiz));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/lecture/{lectureId}")
    public ResponseEntity<List<QuizResponse>> getAllQuizzesByLectureId(@PathVariable UUID lectureId) {
        List<QuizResponse> responses = quizService.getAllQuizzesBylectureId(lectureId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuizResponse> updateQuiz(
            @PathVariable UUID id,
            @RequestBody QuizCreateRequestDTO request) {
        Quiz updated = quizService.updateQuiz(id, request);
        System.out.println(updated);
        if (updated != null) {
            return ResponseEntity.ok(toResponse(updated));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable UUID id) {
        quizService.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<?> createQuiz(@Valid @RequestBody QuizCreateRequestDTO createRequest) {
        try {
            Quiz createdQuiz = quizService.createQuiz(createRequest);
            return ResponseEntity.ok(toResponse(createdQuiz));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
          }
          }
}
