package com.pbl.elearning.enrollment.services.Impl;

import com.pbl.elearning.enrollment.Enum.EnrollmentStatus;
import com.pbl.elearning.enrollment.models.Enrollment;
import com.pbl.elearning.enrollment.payload.request.EnrollmentRequest;
import com.pbl.elearning.enrollment.repository.*;
import com.pbl.elearning.security.domain.User;
import com.pbl.elearning.course.domain.Course;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EnrollmentService - Basic CRUD Operations Tests")
class BasicEnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;
    
    @Mock
    private QuizSubmissionRepository quizSubmissionRepository;
    
    @Mock
    private AssignmentSubmissionRepository assignmentSubmissionRepository;
    
    @Mock
    private CertificateRepository certificateRepository;
    
    @Mock
    private QuizRepository quizRepository;
    
    @Mock
    private AssignmentRepository assignmentRepository;
    
    @Mock
    private ApplicationEventPublisher eventPublisher;

    private EnrollmentServiceImpl enrollmentService;
    
    private UUID userId;
    private UUID courseId;
    private UUID enrollmentId;
    private EnrollmentRequest enrollmentRequest;
    private Enrollment enrollment;
    private User user;
    private Course course;

    @BeforeEach
    void setUp() {
        // Initialize test data
        userId = UUID.randomUUID();
        courseId = UUID.randomUUID();
        enrollmentId = UUID.randomUUID();

        user = new User();
        user.setId(userId);
        user.setEmail("student@example.com");

        course = Course.builder()
                .courseId(courseId)
                .title("Java Programming")
                .description("Learn Java from scratch")
                .build();

        enrollmentRequest = EnrollmentRequest.builder()
                .userId(userId)
                .courseId(courseId)
                .build();

        enrollment = Enrollment.builder()
                .id(enrollmentId)
                .user(user)
                .course(course)
                .status(EnrollmentStatus.ACTIVE)
                .progressPercentage(0.0)
                .enrollmentDate(OffsetDateTime.now())
                .totalWatchTimeMinutes(0)
                .build();
        
        // Create service instance and inject dependencies
        enrollmentService = new EnrollmentServiceImpl(
            enrollmentRepository,
            quizSubmissionRepository,
            assignmentSubmissionRepository,
            certificateRepository,
            quizRepository,
            assignmentRepository
        );
        ReflectionTestUtils.setField(enrollmentService, "eventPublisher", eventPublisher);
    }

    @Test
    @DisplayName("Should create new enrollment successfully")
    void shouldCreateEnrollmentSuccessfully() {
        // Given
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(enrollment);

        // When
        Enrollment result = enrollmentService.createEnrollment(enrollmentRequest);

        // Then
        assertNotNull(result);
        assertEquals(EnrollmentStatus.ACTIVE, result.getStatus());
        assertEquals(0.0, result.getProgressPercentage());
        assertEquals(0, result.getTotalWatchTimeMinutes());
        assertNotNull(result.getEnrollmentDate());
        
        verify(enrollmentRepository, times(1)).save(any(Enrollment.class));
    }

    @Test
    @DisplayName("Should get enrollment by ID successfully")
    void shouldGetEnrollmentByIdSuccessfully() {
        // Given
        when(enrollmentRepository.findById(enrollmentId)).thenReturn(Optional.of(enrollment));

        // When
        Optional<Enrollment> result = enrollmentService.getEnrollmentById(enrollmentId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(enrollmentId, result.get().getId());
        assertEquals(userId, result.get().getUser().getId());
        assertEquals(courseId, result.get().getCourse().getCourseId());
        
        verify(enrollmentRepository, times(1)).findById(enrollmentId);
    }

    @Test
    @DisplayName("Should return empty when enrollment not found")
    void shouldReturnEmptyWhenEnrollmentNotFound() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(enrollmentRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When
        Optional<Enrollment> result = enrollmentService.getEnrollmentById(nonExistentId);

        // Then
        assertFalse(result.isPresent());
        verify(enrollmentRepository, times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("Should get all enrollments successfully")
    void shouldGetAllEnrollmentsSuccessfully() {
        // Given
        Enrollment enrollment2 = Enrollment.builder()
                .id(UUID.randomUUID())
                .user(user)
                .course(course)
                .status(EnrollmentStatus.COMPLETED)
                .progressPercentage(100.0)
                .build();
        
        List<Enrollment> enrollments = Arrays.asList(enrollment, enrollment2);
        when(enrollmentRepository.findAll()).thenReturn(enrollments);

        // When
        List<Enrollment> result = enrollmentService.getAllEnrollments();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(EnrollmentStatus.ACTIVE, result.get(0).getStatus());
        assertEquals(EnrollmentStatus.COMPLETED, result.get(1).getStatus());
        
        verify(enrollmentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get enrollments by user ID successfully")
    void shouldGetEnrollmentsByUserIdSuccessfully() {
        // Given
        List<Enrollment> userEnrollments = Arrays.asList(enrollment);
        when(enrollmentRepository.findByUserId(userId)).thenReturn(userEnrollments);

        // When
        List<Enrollment> result = enrollmentService.getEnrollmentsByUserId(userId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).getUser().getId());
        
        verify(enrollmentRepository, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("Should get enrollments by course ID successfully")
    void shouldGetEnrollmentsByCourseIdSuccessfully() {
        // Given
        List<Enrollment> courseEnrollments = Arrays.asList(enrollment);
        when(enrollmentRepository.findByCourseId(courseId)).thenReturn(courseEnrollments);

        // When
        List<Enrollment> result = enrollmentService.getEnrollmentsByCourseId(courseId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(courseId, result.get(0).getCourse().getCourseId());
        
        verify(enrollmentRepository, times(1)).findByCourseId(courseId);
    }

    @Test
    @DisplayName("Should delete enrollment successfully")
    void shouldDeleteEnrollmentSuccessfully() {
        // Given
        doNothing().when(enrollmentRepository).deleteById(enrollmentId);

        // When
        enrollmentService.deleteEnrollment(enrollmentId);

        // Then
        verify(enrollmentRepository, times(1)).deleteById(enrollmentId);
    }

    @Test
    @DisplayName("Should return empty list when no enrollments found for user")
    void shouldReturnEmptyListWhenNoEnrollmentsFoundForUser() {
        // Given
        UUID nonExistentUserId = UUID.randomUUID();
        when(enrollmentRepository.findByUserId(nonExistentUserId)).thenReturn(Arrays.asList());

        // When
        List<Enrollment> result = enrollmentService.getEnrollmentsByUserId(nonExistentUserId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(enrollmentRepository, times(1)).findByUserId(nonExistentUserId);
    }

    @Test
    @DisplayName("Should return empty list when no enrollments found for course")
    void shouldReturnEmptyListWhenNoEnrollmentsFoundForCourse() {
        // Given
        UUID nonExistentCourseId = UUID.randomUUID();
        when(enrollmentRepository.findByCourseId(nonExistentCourseId)).thenReturn(Arrays.asList());

        // When
        List<Enrollment> result = enrollmentService.getEnrollmentsByCourseId(nonExistentCourseId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(enrollmentRepository, times(1)).findByCourseId(nonExistentCourseId);
    }

    @Test
    @DisplayName("Should handle creation with null values gracefully")
    void shouldHandleCreationWithNullValuesGracefully() {
        // Given
        EnrollmentRequest requestWithNulls = EnrollmentRequest.builder()
                .userId(userId)
                .courseId(courseId)
                .enrollmentDate(null) // null date should be handled
                .build();

        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(enrollment);

        // When
        Enrollment result = enrollmentService.createEnrollment(requestWithNulls);

        // Then
        assertNotNull(result);
        assertEquals(EnrollmentStatus.ACTIVE, result.getStatus());
        assertEquals(0.0, result.getProgressPercentage());
        
        verify(enrollmentRepository, times(1)).save(any(Enrollment.class));
    }
}