// package com.pbl.elearning.enrollment.services.Impl;

// import com.pbl.elearning.enrollment.models.*;
// import com.pbl.elearning.enrollment.payload.request.QuizSubmissionRequest;
// import com.pbl.elearning.enrollment.payload.response.QuizSubmissionResponse;
// import com.pbl.elearning.enrollment.repository.*;
// import com.pbl.elearning.security.domain.User;
// import com.pbl.elearning.security.repository.UserRepository;
// import com.pbl.elearning.user.domain.UserInfo;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.ArgumentCaptor;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;

// import javax.persistence.EntityNotFoundException;
// import java.time.OffsetDateTime;
// import java.time.temporal.ChronoUnit;
// import java.util.Arrays;
// import java.util.List;
// import java.util.Optional;
// import java.util.UUID;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class)
// @DisplayName("QuizSubmissionService - Submit Quiz Tests")
// class QuizSubmissionServiceImplTest {

//     @Mock
//     private QuizSubmissionRepository quizSubmissionRepository;
    
//     @Mock
//     private QuizRepository quizRepository;
    
//     @Mock
//     private QuizQuestionAnswerRepository quizQuestionAnswerRepository;
    
//     @Mock
//     private EnrollmentRepository enrollmentRepository;
    
//     @Mock
//     private UserRepository userRepository;

//     @InjectMocks
//     private QuizSubmissionServiceImpl quizSubmissionService;

//     private UUID quizId;
//     private UUID userId;
//     private UUID enrollmentId;
//     private Quiz quiz;
//     private User user;
//     private Enrollment enrollment;
//     private List<QuizQuestionAnswer> questions;
//     private QuizSubmissionRequest submissionRequest;

//     @BeforeEach
//     void setUp() {
//         quizId = UUID.randomUUID();
//         userId = UUID.randomUUID();
//         enrollmentId = UUID.randomUUID();

//         // Setup User
//         user = new UserInfo();
//         user.setUserId(userId);
//         user.setEmail("student@example.com");

//         // Setup Quiz
//         quiz = Quiz.builder()
//                 .id(quizId)
//                 .title("Java Basics Quiz")
//                 .passingScore(70) // 70 điểm để pass
//                 .maxAttempts(3)
//                 .build();

//         // Setup Enrollment
//         enrollment = Enrollment.builder()
//                 .id(enrollmentId)
//                 .user(user)
//                 .build();

//         // Setup Questions
//         QuizQuestionAnswer question1 = QuizQuestionAnswer.builder()
//                 .id(UUID.randomUUID())
//                 .quiz(quiz)
//                 .questionText("What is Java?")
//                 .correctAnswerIndex(0)
//                 .points(10)
//                 .build();

//         QuizQuestionAnswer question2 = QuizQuestionAnswer.builder()
//                 .id(UUID.randomUUID())
//                 .quiz(quiz)
//                 .questionText("Which company developed Java?")
//                 .correctAnswerIndex(1)
//                 .points(15)
//                 .build();
        
//         // CÂU HỎI 3 (ĐỂ TEST TRƯỜNG HỢP PASS)
//         QuizQuestionAnswer question3 = QuizQuestionAnswer.builder()
//                 .id(UUID.randomUUID())
//                 .quiz(quiz)
//                 .questionText("What is JVM?")
//                 .correctAnswerIndex(2)
//                 .points(75) // 10 + 15 + 75 = 100
//                 .build();

//         questions = Arrays.asList(question1, question2, question3);

//         // Setup Submission Request (Trả lời 1 đúng, 2 sai)
//         QuizSubmissionRequest.QuizAnswerRequest answer1 = QuizSubmissionRequest.QuizAnswerRequest.builder()
//                 .questionId(question1.getId())
//                 .selectedAnswerIndex(0) // Correct (10 điểm)
//                 .build();

//         QuizSubmissionRequest.QuizAnswerRequest answer2 = QuizSubmissionRequest.QuizAnswerRequest.builder()
//                 .questionId(question2.getId())
//                 .selectedAnswerIndex(2) // Wrong (0 điểm)
//                 .build();
        
//         // Không trả lời câu 3 (để test maxPossibleScore)

//         submissionRequest = QuizSubmissionRequest.builder()
//                 .quizId(quizId)
//                 .enrollmentId(enrollmentId)
//                 .startedAt(OffsetDateTime.now().minus(10, ChronoUnit.MINUTES))
//                 .answers(Arrays.asList(answer1, answer2)) // Chỉ nộp 2/3 câu trả lời
//                 .build();
//     }

//     @Test
//     @DisplayName("Should submit quiz, calculate score correctly, and fail")
//     void shouldSuccessfullySubmitQuizAndCalculateScoreAndFail() {
//         // Given
//         when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
//         when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//         when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));
//         when(quizSubmissionRepository.countByQuizAndUser(quiz, user)).thenReturn(0); // Lần nộp đầu tiên
//         when(quizQuestionAnswerRepository.findByQuiz(quiz)).thenReturn(questions);
        
//         // *** FIX QUAN TRỌNG ***
//         // Dạy cho repo: Bất cứ thứ gì được save, hãy trả về chính nó
//         when(quizSubmissionRepository.save(any(QuizSubmission.class))).thenAnswer(inv -> inv.getArgument(0));

//         // Tạo ArgumentCaptor để bắt đối tượng
//         ArgumentCaptor<QuizSubmission> submissionCaptor = ArgumentCaptor.forClass(QuizSubmission.class);

//         // When
//         QuizSubmissionResponse result = quizSubmissionService.submitQuiz(submissionRequest, userId);

//         // Then
//         // 1. Kiểm tra đối tượng THẬT được lưu
//         verify(quizSubmissionRepository).save(submissionCaptor.capture());
//         QuizSubmission capturedSubmission = submissionCaptor.getValue();
        
//         assertNotNull(capturedSubmission);
//         assertEquals(1, capturedSubmission.getAttemptNumber());
//         assertEquals(10, capturedSubmission.getTotalScore()); // Chỉ câu 1 đúng (10 điểm)
        
//         // maxPossibleScore là tổng điểm của các câu HỌC SINH ĐÃ NỘP (q1 + q2)
//         assertEquals(25, capturedSubmission.getMaxPossibleScore()); // 10 + 15 
        
//         assertEquals(40.0, capturedSubmission.getScorePercentage(), 0.01); // 10/25 * 100
//         assertEquals(false, capturedSubmission.getIsPassed()); // 10 điểm < 70 điểm (passingScore)
//         assertEquals(true, capturedSubmission.getIsCompleted());
//         assertTrue(capturedSubmission.getTimeTakenMinutes() >= 9 && capturedSubmission.getTimeTakenMinutes() <= 11);
        
//         // 2. Kiểm tra các câu trả lời con
//         assertEquals(2, capturedSubmission.getAnswers().size());
//         QuizAnswer firstAnswer = capturedSubmission.getAnswers().get(0);
//         assertEquals(true, firstAnswer.getIsCorrect());
//         assertEquals(10, firstAnswer.getPointsEarned());

//         QuizAnswer secondAnswer = capturedSubmission.getAnswers().get(1);
//         assertEquals(false, secondAnswer.getIsCorrect());
//         assertEquals(0, secondAnswer.getPointsEarned());
        
//         // 3. (Tùy chọn) Kiểm tra Response DTO (nếu hàm convertToResponse của bạn đơn giản)
//         assertNotNull(result);
//         assertEquals(10, result.getTotalScore());
//         assertEquals(false, result.getIsPassed());
//     }

//     @Test
//     @DisplayName("Should mark quiz as passed when score meets threshold")
//     void shouldMarkQuizAsPassedWhenScoreMeetsThreshold() {
//         // Given - Trả lời đúng 2 câu (q1, q3)
//         QuizSubmissionRequest.QuizAnswerRequest answer1 = QuizSubmissionRequest.QuizAnswerRequest.builder()
//                 .questionId(questions.get(0).getId())
//                 .selectedAnswerIndex(0) // Correct (10 điểm)
//                 .build();
//         QuizSubmissionRequest.QuizAnswerRequest answer3 = QuizSubmissionRequest.QuizAnswerRequest.builder()
//                 .questionId(questions.get(2).getId())
//                 .selectedAnswerIndex(2) // Correct (75 điểm)
//                 .build();
//         submissionRequest.setAnswers(Arrays.asList(answer1, answer3));

//         when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
//         when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//         when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));
//         when(quizSubmissionRepository.countByQuizAndUser(quiz, user)).thenReturn(1); // Lần nộp thứ 2
//         when(quizQuestionAnswerRepository.findByQuiz(quiz)).thenReturn(questions);
        
//         // *** FIX QUAN TRỌNG ***
//         when(quizSubmissionRepository.save(any(QuizSubmission.class))).thenAnswer(inv -> inv.getArgument(0));
        
//         ArgumentCaptor<QuizSubmission> submissionCaptor = ArgumentCaptor.forClass(QuizSubmission.class);

//         // When
//         QuizSubmissionResponse result = quizSubmissionService.submitQuiz(submissionRequest, userId);

//         // Then
//         verify(quizSubmissionRepository).save(submissionCaptor.capture());
//         QuizSubmission capturedSubmission = submissionCaptor.getValue();
        
//         assertEquals(2, capturedSubmission.getAttemptNumber()); // Lần nộp thứ 2
//         assertEquals(85, capturedSubmission.getTotalScore()); // 10 + 75
//         assertEquals(85, capturedSubmission.getMaxPossibleScore()); // 10 + 75
//         assertEquals(100.0, capturedSubmission.getScorePercentage(), 0.01);
//         assertEquals(true, capturedSubmission.getIsPassed()); // 85 điểm > 70 điểm
//     }

//     @Test
//     @DisplayName("Should throw exception when quiz not found")
//     void shouldThrowExceptionWhenQuizNotFound() {
//         // Given
//         when(quizRepository.findById(quizId)).thenReturn(Optional.empty());

//         // When & Then
//         EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
//                 () -> quizSubmissionService.submitQuiz(submissionRequest, userId));
        
//         assertEquals("Không tìm thấy Quiz: " + quizId, exception.getMessage());
//     }

//     @Test
//     @DisplayName("Should throw exception when user not found")
//     void shouldThrowExceptionWhenUserNotFound() {
//         // Given
//         when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
//         when(userRepository.findById(userId)).thenReturn(Optional.empty());

//         // When & Then
//         EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
//                 () -> quizSubmissionService.submitQuiz(submissionRequest, userId));
        
//         assertEquals("Không tìm thấy User: " + userId, exception.getMessage());
//     }

//     @Test
//     @DisplayName("Should throw exception when enrollment not found")
//     void shouldThrowExceptionWhenEnrollmentNotFound() {
//         // Given
//         when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
//         when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//         when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.empty());

//         // When & Then
//         EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
//                 () -> quizSubmissionService.submitQuiz(submissionRequest, userId));
        
//         assertEquals("Không tìm thấy Enrollment: " + enrollmentId, exception.getMessage());
//     }

//     @Test
//     @DisplayName("Should throw exception when max attempts exceeded")
//     void shouldThrowExceptionWhenMaxAttemptsExceeded() {
//         // Given
//         when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
//         when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//         when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));
//         when(quizSubmissionRepository.countByQuizAndUser(quiz, user)).thenReturn(3); // Already 3 attempts, max is 3

//         // When & Then
//         IllegalStateException exception = assertThrows(IllegalStateException.class,
//                 () -> quizSubmissionService.submitQuiz(submissionRequest, userId));
        
//         assertEquals("Bạn đã hết số lần làm bài cho quiz này.", exception.getMessage());
//     }

//     @Test
//     @DisplayName("Should allow submission when max attempts is null (unlimited)")
//     void shouldAllowSubmissionWhenMaxAttemptsIsNull() {
//             // Given
//             quiz.setMaxAttempts(null); // Không giới hạn

//             when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
//             when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//             when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));
//             when(quizSubmissionRepository.countByQuizAndUser(quiz, user)).thenReturn(10); // Đã nộp 10 lần
//             when(quizQuestionAnswerRepository.findByQuiz(quiz)).thenReturn(questions);

//             // *** FIX QUAN TRỌNG ***
//             when(quizSubmissionRepository.save(any(QuizSubmission.class))).thenAnswer(inv -> inv.getArgument(0));

//             ArgumentCaptor<QuizSubmission> submissionCaptor = ArgumentCaptor.forClass(QuizSubmission.class);

//             // When
//             quizSubmissionService.submitQuiz(submissionRequest, userId);

//             // Then
//             verify(quizSubmissionRepository).save(submissionCaptor.capture());
//             QuizSubmission capturedSubmission = submissionCaptor.getValue();

//             assertEquals(11, capturedSubmission.getAttemptNumber()); // Phải là lần nộp thứ 11
//     }
    
//     @Test
//     @DisplayName("Should handle null start time gracefully")
//     void shouldHandleNullStartTimeGracefully() {
//         // (Test này của bạn đã dùng ArgumentCaptor và viết đúng, giữ nguyên)
//         // Given
//         submissionRequest.setStartedAt(null);

//         when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));
//         when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//         when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));
//         when(quizSubmissionRepository.countByQuizAndUser(quiz, user)).thenReturn(0);
//         when(quizQuestionAnswerRepository.findByQuiz(quiz)).thenReturn(questions);
        
//         when(quizSubmissionRepository.save(any(QuizSubmission.class))).thenAnswer(inv -> inv.getArgument(0));

//         // When
//         quizSubmissionService.submitQuiz(submissionRequest, userId);

//         // Then
//         ArgumentCaptor<QuizSubmission> submissionCaptor = ArgumentCaptor.forClass(QuizSubmission.class);
//         verify(quizSubmissionRepository).save(submissionCaptor.capture());
//         QuizSubmission capturedSubmission = submissionCaptor.getValue();
        
//         assertNull(capturedSubmission.getTimeTakenMinutes()); // Kiểm tra timeTaken là null
//     }
// }