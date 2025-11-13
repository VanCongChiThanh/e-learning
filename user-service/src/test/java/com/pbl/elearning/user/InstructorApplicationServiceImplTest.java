package com.pbl.elearning.user;

import com.pbl.elearning.common.domain.enums.Role;
import com.pbl.elearning.common.exception.ConflictException;
import com.pbl.elearning.common.exception.ForbiddenException;
import com.pbl.elearning.email.service.EmailService;
import com.pbl.elearning.user.domain.InstructorApplication;
import com.pbl.elearning.user.domain.enums.ApplicationStatus;
import com.pbl.elearning.user.payload.request.instructor.ApplyInstructorRequest;
import com.pbl.elearning.user.payload.response.instructor.ApplyInstructorResponse;
import com.pbl.elearning.user.repository.InstructorApplicationRepository;
import com.pbl.elearning.user.service.UserInfoService;
import com.pbl.elearning.user.service.UserService;
import com.pbl.elearning.user.service.impl.InstructorApplicationServiceImpl;
import com.pbl.elearning.user.service.impl.InstructorProfileServiceImpl;
import com.pbl.elearning.common.exception.BadRequestException;
import com.pbl.elearning.security.domain.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class InstructorApplicationServiceImplTest {

    @Mock private InstructorApplicationRepository instructorApplicationRepository;
    @Mock private InstructorProfileServiceImpl instructorProfileService;
    @Mock private EmailService emailService;
    @Mock private UserInfoService userInfoService;
    @Mock private UserService userService;

    @InjectMocks
    private InstructorApplicationServiceImpl service;

    private UUID userId;
    private User verifiedLearner;
    private ApplyInstructorRequest baseRequest;

    @BeforeEach
    void init() {
        userId = UUID.randomUUID();

        verifiedLearner = new User();
        verifiedLearner.setId(userId);
        verifiedLearner.setRole(Role.ROLE_LEARNER);
        verifiedLearner.setIsEnabled(true);
        verifiedLearner.setConfirmedAt(Timestamp.from(Instant.now().minusSeconds(60)));

        baseRequest = new ApplyInstructorRequest();
        baseRequest.setPortfolioLink("https://portfolio.example.com/user123");
        baseRequest.setMotivation("I have 10+ years of teaching experience in Java and Spring Boot.");
        baseRequest.setCvUrl("s3://bucket/cv.pdf");
    }

    // ============ HAPPY PATH ============

    @Test
    void applyForInstructor_ok_whenVerifiedLearner_noPreviousApplication_savesAndReturns() {
        when(userService.findById(userId)).thenReturn(verifiedLearner);
        when(instructorApplicationRepository.findTopByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(Optional.empty());

        ArgumentCaptor<InstructorApplication> appCap = ArgumentCaptor.forClass(InstructorApplication.class);
        when(instructorApplicationRepository.save(appCap.capture()))
                .thenAnswer(inv -> {
                    InstructorApplication a = inv.getArgument(0);
                    a.setId(UUID.randomUUID());
                    return a;
                });

        ApplyInstructorResponse resp = service.applyForInstructor(baseRequest, userId);

        assertNotNull(resp);
        InstructorApplication saved = appCap.getValue();
        assertEquals(userId, saved.getUserId());
        assertEquals(baseRequest.getPortfolioLink(), saved.getPortfolioLink());
        assertEquals(baseRequest.getMotivation(), saved.getMotivation());
        assertEquals(baseRequest.getCvUrl(), saved.getCvUrl());
        assertEquals(ApplicationStatus.PENDING, saved.getStatus());

        verify(instructorApplicationRepository, times(1)).save(any(InstructorApplication.class));
    }

    // ============ ROLE / STATUS GUARDS ============

    @Test
    void applyForInstructor_reject_whenAlreadyInstructor() {
        User instructor = new User();
        instructor.setId(userId);
        instructor.setRole(Role.ROLE_INSTRUCTOR);
        instructor.setIsEnabled(true);
        instructor.setConfirmedAt(Timestamp.from(Instant.now()));

        when(userService.findById(userId)).thenReturn(instructor);

        assertThrows(ForbiddenException.class,
                () -> service.applyForInstructor(baseRequest, userId));

        verify(instructorApplicationRepository, never()).save(any());
    }

    @Test
    void applyForInstructor_reject_whenUserNotEnabled() {
        User u = new User();
        u.setId(userId);
        u.setRole(Role.ROLE_LEARNER);
        u.setIsEnabled(false);
        u.setConfirmedAt(Timestamp.from(Instant.now()));

        when(userService.findById(userId)).thenReturn(u);

        assertThrows(ForbiddenException.class,
                () -> service.applyForInstructor(baseRequest, userId));

        verify(instructorApplicationRepository, never()).save(any());
    }

    @Test
    void applyForInstructor_reject_whenUserNotConfirmed() {
        User u = new User();
        u.setId(userId);
        u.setRole(Role.ROLE_LEARNER);
        u.setIsEnabled(true);
        u.setConfirmedAt(null);

        when(userService.findById(userId)).thenReturn(u);

        assertThrows(ForbiddenException.class,
                () -> service.applyForInstructor(baseRequest, userId));

        verify(instructorApplicationRepository, never()).save(any());
    }

    // ============ PREVIOUS APPLICATION STATES ============

    @Test
    void applyForInstructor_reject_whenLastApplicationPending() {
        when(userService.findById(userId)).thenReturn(verifiedLearner);

        InstructorApplication last = new InstructorApplication();
        last.setUserId(userId);
        last.setStatus(ApplicationStatus.PENDING);
        last.setUpdatedAt(Timestamp.from(Instant.now().minus(Duration.ofDays(1))));

        when(instructorApplicationRepository.findTopByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(Optional.of(last));

        assertThrows(ConflictException.class,
                () -> service.applyForInstructor(baseRequest, userId));

        verify(instructorApplicationRepository, never()).save(any());
    }

    @Test
    void applyForInstructor_reject_whenLastApplicationApproved() {
        when(userService.findById(userId)).thenReturn(verifiedLearner);

        InstructorApplication last = new InstructorApplication();
        last.setUserId(userId);
        last.setStatus(ApplicationStatus.APPROVED);
        last.setUpdatedAt(Timestamp.from(Instant.now().minus(Duration.ofDays(1))));

        when(instructorApplicationRepository.findTopByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(Optional.of(last));

        assertThrows(ConflictException.class,
                () -> service.applyForInstructor(baseRequest, userId));

        verify(instructorApplicationRepository, never()).save(any());
    }

    @Test
    void applyForInstructor_reject_whenLastApplicationRejectedWithinCooldown() {
        when(userService.findById(userId)).thenReturn(verifiedLearner);

        InstructorApplication last = new InstructorApplication();
        last.setUserId(userId);
        last.setStatus(ApplicationStatus.REJECTED);
        // 5 days ago < 14 days cooldown
        last.setUpdatedAt(Timestamp.from(Instant.now().minus(Duration.ofDays(5))));

        when(instructorApplicationRepository.findTopByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(Optional.of(last));

        assertThrows(ConflictException.class,
                () -> service.applyForInstructor(baseRequest, userId));

        verify(instructorApplicationRepository, never()).save(any());
    }

    @Test
    void applyForInstructor_ok_whenLastApplicationRejectedBeyondCooldown() {
        when(userService.findById(userId)).thenReturn(verifiedLearner);

        InstructorApplication last = new InstructorApplication();
        last.setUserId(userId);
        last.setStatus(ApplicationStatus.REJECTED);
        // 20 days ago > 14 days cooldown
        last.setUpdatedAt(Timestamp.from(Instant.now().minus(Duration.ofDays(20))));

        when(instructorApplicationRepository.findTopByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(Optional.of(last));

        when(instructorApplicationRepository.save(any(InstructorApplication.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> service.applyForInstructor(baseRequest, userId));
        verify(instructorApplicationRepository, times(1)).save(any());
    }

    // ============ BOUNDARY TESTS ============

    // ---- Cooldown 14 days boundary ----
    @Test
    void applyForInstructor_reject_whenRejected_justBefore14Days() {
        when(userService.findById(userId)).thenReturn(verifiedLearner);

        InstructorApplication last = new InstructorApplication();
        last.setUserId(userId);
        last.setStatus(ApplicationStatus.REJECTED);
        // 13 days 23h 59m ago -> still < 14d => reject
        last.setUpdatedAt(Timestamp.from(
                Instant.now().minus(Duration.ofDays(13)).minus(Duration.ofMinutes(1))
        ));

        when(instructorApplicationRepository.findTopByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(Optional.of(last));

        assertThrows(ConflictException.class,
                () -> service.applyForInstructor(baseRequest, userId));

        verify(instructorApplicationRepository, never()).save(any());
    }

    @Test
    void applyForInstructor_ok_whenRejected_exactlyAt14DaysBoundary() {
        when(userService.findById(userId)).thenReturn(verifiedLearner);

        InstructorApplication last = new InstructorApplication();
        last.setUserId(userId);
        last.setStatus(ApplicationStatus.REJECTED);
        // exactly 14d ago -> updatedAt.plus(14d).isAfter(now) == false => allowed
        last.setUpdatedAt(Timestamp.from(Instant.now().minus(Duration.ofDays(14))));

        when(instructorApplicationRepository.findTopByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(Optional.of(last));
        when(instructorApplicationRepository.save(any(InstructorApplication.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> service.applyForInstructor(baseRequest, userId));
        verify(instructorApplicationRepository, times(1)).save(any());
    }

    @Test
    void applyForInstructor_ok_whenRejected_over14Days() {
        when(userService.findById(userId)).thenReturn(verifiedLearner);

        InstructorApplication last = new InstructorApplication();
        last.setUserId(userId);
        last.setStatus(ApplicationStatus.REJECTED);
        // 14d + 1m ago -> allowed
        last.setUpdatedAt(Timestamp.from(
                Instant.now().minus(Duration.ofDays(14)).minus(Duration.ofMinutes(1))
        ));

        when(instructorApplicationRepository.findTopByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(Optional.of(last));
        when(instructorApplicationRepository.save(any(InstructorApplication.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> service.applyForInstructor(baseRequest, userId));
        verify(instructorApplicationRepository, times(1)).save(any());
    }

    // ---- Motivation length boundary (min = 50) ----
    @Test
    void applyForInstructor_reject_whenMotivationLen49() {
        when(userService.findById(userId)).thenReturn(verifiedLearner);
        when(instructorApplicationRepository.findTopByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(Optional.empty());

        ApplyInstructorRequest bad = new ApplyInstructorRequest();
        bad.setPortfolioLink("https://portfolio.example.com/x");
        bad.setMotivation("x".repeat(49)); // 49 chars
        bad.setCvUrl("s3://bucket/cv.pdf");

        assertThrows(BadRequestException.class,
                () -> service.applyForInstructor(bad, userId));
        verify(instructorApplicationRepository, never()).save(any());
    }

    @Test
    void applyForInstructor_ok_whenMotivationLen50() {
        when(userService.findById(userId)).thenReturn(verifiedLearner);
        when(instructorApplicationRepository.findTopByUserIdOrderByCreatedAtDesc(userId))
                .thenReturn(Optional.empty());
        when(instructorApplicationRepository.save(any(InstructorApplication.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        ApplyInstructorRequest good = new ApplyInstructorRequest();
        good.setPortfolioLink("https://portfolio.example.com/x");
        good.setMotivation("x".repeat(50)); // 50 chars
        good.setCvUrl("s3://bucket/cv.pdf");

        assertDoesNotThrow(() -> service.applyForInstructor(good, userId));
        verify(instructorApplicationRepository, times(1)).save(any());
    }
}