package com.pbl.elearning.enrollment.services;

import com.pbl.elearning.enrollment.models.Quiz;
import com.pbl.elearning.enrollment.payload.request.QuizRequest;

import java.util.List;
import java.util.UUID;

public interface QuizService {
    // 1. Tạo quiz mới
    Quiz createQuiz(QuizRequest request);

    // 2. Lấy quiz theo id
    Quiz getQuizById(UUID id);

    // 3. Lấy danh sách tất cả quiz
    List<Quiz> getAllQuizzesBylectureId(UUID id);

    // 4. Cập nhật quiz
    Quiz updateQuiz(UUID id, QuizRequest request);

    // 5. Xóa quiz
    void deleteQuiz(UUID id);
}
