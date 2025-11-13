package com.pbl.elearning.enrollment.services.Impl;

import com.pbl.elearning.enrollment.models.Quiz;
import com.pbl.elearning.enrollment.models.QuizQuestionAnswer;
import com.pbl.elearning.enrollment.payload.request.QuestionCreateRequest;
import com.pbl.elearning.enrollment.payload.request.QuizCreateRequestDTO;
import com.pbl.elearning.enrollment.repository.QuizQuestionAnswerRepository;
import com.pbl.elearning.enrollment.repository.QuizRepository;
import com.pbl.elearning.course.domain.Lecture;
import com.pbl.elearning.course.repository.LectureRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("QuizService - Create Quiz Tests")
class QuizServiceImplTest {

    // 1. Tạo các mock cho các dependency
    @Mock
    private QuizRepository quizRepository;
    
    @Mock
    private LectureRepository lectureRepository;
    
    @Mock
    private QuizQuestionAnswerRepository questionRepository;

    @InjectMocks
    private QuizServiceImpl quizService;

    private UUID lectureId;
    private Lecture lecture;
    private QuizCreateRequestDTO quizCreateRequest;
    private Quiz savedQuiz;

    @BeforeEach
    void setUp() {
        lectureId = UUID.randomUUID();
        
        lecture = Lecture.builder()
                .lectureId(lectureId)
                .title("Introduction to Java")
                .duration(60)
                .build();

        // Tạo questions cho quiz
        QuestionCreateRequest question1 = QuestionCreateRequest.builder()
                .questionText("What is Java?")
                .options(Arrays.asList("Programming Language", "Coffee", "Island", "Framework"))
                .correctAnswerIndex(0)
                .points(10)
                .sortOrder(1)
                .build();

        QuestionCreateRequest question2 = QuestionCreateRequest.builder()
                .questionText("Which company developed Java?")
                .options(Arrays.asList("Microsoft", "Oracle", "Google", "Apple"))
                .correctAnswerIndex(1)
                .points(10)
                .sortOrder(2)
                .build();

        quizCreateRequest = QuizCreateRequestDTO.builder()
                .lectureId(lectureId)
                .title("Java Basics Quiz")
                .description("Test your knowledge of Java basics")
                .timeLimitMinutes(30)
                .passingScore(70)
                .maxAttempts(3)
                .questions(new ArrayList<>(Arrays.asList(question1, question2)))
                .build();

        // Đây là đối tượng *mẫu* sẽ được trả về khi gọi quizRepository.save()
        savedQuiz = Quiz.builder()
                .id(UUID.randomUUID()) // ID giả lập
                .lecture(lecture)
                .title(quizCreateRequest.getTitle())
                .description(quizCreateRequest.getDescription())
                .timeLimitMinutes(quizCreateRequest.getTimeLimitMinutes())
                .passingScore(quizCreateRequest.getPassingScore())
                .maxAttempts(quizCreateRequest.getMaxAttempts())
                .numberQuestions(quizCreateRequest.getQuestions().size())
                .createdAt(OffsetDateTime.now()) // Thời gian giả lập
                .build();
        
    }

    @Test
    @DisplayName("Should successfully create quiz with questions")
    void shouldSuccessfullyCreateQuizWithQuestions() {
        // === GIVEN ===
        // Dạy cho mock repository biết phải làm gì
        when(lectureRepository.findById(lectureId)).thenReturn(Optional.of(lecture));
        when(quizRepository.save(any(Quiz.class))).thenReturn(savedQuiz);
        when(questionRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

        // Tạo ArgumentCaptor để "bắt" các đối tượng thật được truyền đi
        ArgumentCaptor<Quiz> quizCaptor = ArgumentCaptor.forClass(Quiz.class);
        ArgumentCaptor<List<QuizQuestionAnswer>> questionsCaptor = ArgumentCaptor.forClass(List.class);

        // === WHEN ===
        Quiz result = quizService.createQuiz(quizCreateRequest);

        // === THEN ===

        // 1. Kiểm tra kết quả trả về (phải là đối tượng savedQuiz)
        assertNotNull(result);
        assertEquals(savedQuiz.getId(), result.getId());
        assertEquals("Java Basics Quiz", result.getTitle());

        // 2. Kiểm tra đối tượng Quiz *thật* được truyền vào hàm save
        //    (Đây là phần quan trọng nhất để kiểm tra logic mapping)
        verify(quizRepository, times(1)).save(quizCaptor.capture());
        Quiz quizBeingSaved = quizCaptor.getValue();

        assertEquals("Java Basics Quiz", quizBeingSaved.getTitle());
        assertEquals("Test your knowledge of Java basics", quizBeingSaved.getDescription());
        assertEquals(30, quizBeingSaved.getTimeLimitMinutes());
        assertEquals(70, quizBeingSaved.getPassingScore());
        assertEquals(3, quizBeingSaved.getMaxAttempts());
        assertEquals(2, quizBeingSaved.getNumberQuestions()); // Kiểm tra logic .size()
        assertEquals(lecture, quizBeingSaved.getLecture());
        assertNotNull(quizBeingSaved.getCreatedAt());

        // 3. Kiểm tra danh sách Questions *thật* được truyền vào hàm saveAll
        verify(questionRepository, times(1)).saveAll(questionsCaptor.capture());
        List<QuizQuestionAnswer> savedQuestions = questionsCaptor.getValue();

        assertEquals(2, savedQuestions.size());

        // Kiểm tra câu hỏi 1
        QuizQuestionAnswer firstQuestion = savedQuestions.get(0);
        assertEquals("What is Java?", firstQuestion.getQuestionText());
        assertEquals(Arrays.asList("Programming Language", "Coffee", "Island", "Framework"),
                firstQuestion.getOptions());
        assertEquals(0, firstQuestion.getCorrectAnswerIndex());
        assertEquals(10, firstQuestion.getPoints());
        assertEquals(1, firstQuestion.getSortOrder());
        assertNotNull(firstQuestion.getCreatedAt());
        assertEquals(savedQuiz, firstQuestion.getQuiz()); // Đảm bảo question được gán đúng quiz

        // Kiểm tra câu hỏi 2
        QuizQuestionAnswer secondQuestion = savedQuestions.get(1);
        assertEquals("Which company developed Java?", secondQuestion.getQuestionText());
        assertEquals(1, secondQuestion.getCorrectAnswerIndex());
        assertNotNull(secondQuestion.getCreatedAt());
        assertEquals(savedQuiz, secondQuestion.getQuiz());
    }
    
    @Test
    @DisplayName("Should set number of questions correctly based on questions list size")
    void shouldSetNumberOfQuestionsCorrectly() {
        // Given
        QuestionCreateRequest question3 = QuestionCreateRequest.builder()
                .questionText("Is Java platform independent?")
                .options(Arrays.asList("Yes", "No"))
                .correctAnswerIndex(0)
                .points(5)
                .sortOrder(3)
                .build();

        quizCreateRequest.getQuestions().add(question3);

        when(lectureRepository.findById(lectureId)).thenReturn(Optional.of(lecture));
        when(quizRepository.save(any(Quiz.class))).thenReturn(savedQuiz);
        when(questionRepository.saveAll(anyList())).thenReturn(Arrays.asList());

        // When
        Quiz result = quizService.createQuiz(quizCreateRequest);

        // Then
        verify(quizRepository).save(argThat(quiz -> quiz.getNumberQuestions() == 3));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when lecture not found")
    void shouldThrowEntityNotFoundExceptionWhenLectureNotFound() {
        // Given
        UUID nonExistentLectureId = UUID.randomUUID();
        quizCreateRequest.setLectureId(nonExistentLectureId);

        // Dạy cho mock trả về rỗng (empty)
        when(lectureRepository.findById(nonExistentLectureId)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> quizService.createQuiz(quizCreateRequest));

        assertEquals("Không tìm thấy Lecture với ID: " + nonExistentLectureId, exception.getMessage());

        // Verify (Đảm bảo không có gì được lưu)
        verify(quizRepository, never()).save(any(Quiz.class));
        verify(questionRepository, never()).saveAll(anyList());
    }
    
    @Test
    @DisplayName("Should create quiz with empty questions list")
    void shouldCreateQuizWithEmptyQuestionsList() {
        // Given
        quizCreateRequest.setQuestions(Collections.emptyList());
        
        when(lectureRepository.findById(lectureId)).thenReturn(Optional.of(lecture));
        when(quizRepository.save(any(Quiz.class))).thenReturn(savedQuiz);
        when(questionRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

        // When
        Quiz result = quizService.createQuiz(quizCreateRequest);

        // Then
        assertNotNull(result);
        
        // Dùng argThat để kiểm tra nhanh giá trị bên trong đối tượng được save
        verify(quizRepository).save(argThat(quiz -> quiz.getNumberQuestions() == 0));
        verify(questionRepository).saveAll(argThat(list -> !list.iterator().hasNext()));
    }
    @Test
    @DisplayName("Should handle null values in quiz creation gracefully")
    void shouldHandleNullValuesGracefully() {
        // Given
        QuizCreateRequestDTO requestWithNulls = QuizCreateRequestDTO.builder()
                .lectureId(lectureId)
                .title("Basic Quiz")
                .description(null)
                .timeLimitMinutes(null)
                .passingScore(null)
                .maxAttempts(null)
                .questions(Arrays.asList())
                .build();

        Quiz quizWithNulls = Quiz.builder()
                .id(UUID.randomUUID())
                .lecture(lecture)
                .title("Basic Quiz")
                .description(null)
                .timeLimitMinutes(null)
                .passingScore(null)
                .maxAttempts(null)
                .numberQuestions(0)
                .createdAt(OffsetDateTime.now())
                .build();

        when(lectureRepository.findById(lectureId)).thenReturn(Optional.of(lecture));
        when(quizRepository.save(any(Quiz.class))).thenReturn(quizWithNulls);
        when(questionRepository.saveAll(anyList())).thenReturn(Arrays.asList());

        // When
        Quiz result = quizService.createQuiz(requestWithNulls);

        // Then
        assertNotNull(result);
        assertEquals("Basic Quiz", result.getTitle());
        assertNull(result.getDescription());
        assertNull(result.getTimeLimitMinutes());
        assertNull(result.getPassingScore());
        assertNull(result.getMaxAttempts());
        assertEquals(0, result.getNumberQuestions());
    }
}