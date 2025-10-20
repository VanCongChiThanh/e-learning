package com.pbl.elearning.web.endpoint.enrollment;

import com.pbl.elearning.enrollment.models.Quiz;
import com.pbl.elearning.enrollment.payload.request.QuizQuestionAnswerRequest;
import com.pbl.elearning.enrollment.payload.request.BulkQuizQuestionsRequest;
import com.pbl.elearning.enrollment.payload.response.QuizQuestionAnswerResponse;
import com.pbl.elearning.enrollment.services.QuizQuestionAnswerService;
import com.pbl.elearning.enrollment.services.QuizService;
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
    private final QuizService quizService;

    @PostMapping("/{id}")
    public ResponseEntity<QuizQuestionAnswerResponse> create(@PathVariable UUID id,
            @RequestBody QuizQuestionAnswerRequest request) {
        return ResponseEntity.ok(service.createQuizQuestionAnswer(id, request));
    }

    @PostMapping("/{quizId}/bulk")
    public ResponseEntity<?> createBulkQuestions(@PathVariable UUID quizId,
            @RequestBody BulkQuizQuestionsRequest request) {
        
        // Lấy thông tin quiz để kiểm tra numberQuestions
        Quiz quiz = quizService.getQuizById(quizId);
        if (quiz == null) {
            return ResponseEntity.badRequest().body("Quiz không tồn tại với ID: " + quizId);
        }

        // Kiểm tra số lượng câu hỏi hiện tại
        List<QuizQuestionAnswerResponse> existingQuestions = service.getAllByQuizId(quizId);
        int currentQuestionCount = existingQuestions.size();
        int newQuestionCount = request.getQuestions().size();
        int totalAfterAdd = currentQuestionCount + newQuestionCount;

        // Kiểm tra xem có vượt quá số lượng câu hỏi cho phép không
        if (totalAfterAdd > quiz.getNumberQuestions()) {
            return ResponseEntity.badRequest().body(
                String.format("Không thể thêm %d câu hỏi. Quiz này chỉ cho phép tối đa %d câu hỏi. " +
                             "Hiện tại đã có %d câu hỏi. Bạn chỉ có thể thêm tối đa %d câu hỏi nữa.",
                             newQuestionCount, quiz.getNumberQuestions(), currentQuestionCount, 
                             quiz.getNumberQuestions() - currentQuestionCount)
            );
        }

        // Kiểm tra xem có đủ số lượng câu hỏi như quiz yêu cầu không
        if (totalAfterAdd < quiz.getNumberQuestions()) {
            return ResponseEntity.badRequest().body(
                String.format("Chưa đủ số lượng câu hỏi. Quiz này yêu cầu %d câu hỏi. " +
                             "Hiện tại có %d câu hỏi, thêm %d câu nữa sẽ có %d câu. " +
                             "Vui lòng thêm đủ %d câu hỏi.",
                             quiz.getNumberQuestions(), currentQuestionCount, newQuestionCount, 
                             totalAfterAdd, quiz.getNumberQuestions())
            );
        }

        // Nếu đúng số lượng, thực hiện thêm câu hỏi
        List<QuizQuestionAnswerResponse> responses = service.createBulkQuizQuestions(quizId, request);
        return ResponseEntity.ok(responses);
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