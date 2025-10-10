package com.pbl.elearning.enrollment.services.Impl;

import com.pbl.elearning.enrollment.Enum.EnrollmentStatus;
import com.pbl.elearning.enrollment.models.*;
import com.pbl.elearning.enrollment.payload.request.EnrollmentRequest;
import com.pbl.elearning.enrollment.payload.request.UpdateEnrollmentRequest;
import com.pbl.elearning.enrollment.payload.response.EnrollmentReportResponse;
import com.pbl.elearning.enrollment.repository.*;
import com.pbl.elearning.enrollment.services.EnrollmentService;
import com.pbl.elearning.security.domain.User;
import com.pbl.elearning.course.domain.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final QuizSubmissionRepository quizSubmissionRepository;
    private final AssignmentSubmissionRepository assignmentSubmissionRepository;
    private final CertificateRepository certificateRepository;
    private final QuizRepository quizRepository;
    private final AssignmentRepository assignmentRepository;

    @Autowired
    public EnrollmentServiceImpl(
            EnrollmentRepository enrollmentRepository,
            QuizSubmissionRepository quizSubmissionRepository,
            AssignmentSubmissionRepository assignmentSubmissionRepository,
            CertificateRepository certificateRepository,
            QuizRepository quizRepository,
            AssignmentRepository assignmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.quizSubmissionRepository = quizSubmissionRepository;
        this.assignmentSubmissionRepository = assignmentSubmissionRepository;
        this.certificateRepository = certificateRepository;
        this.quizRepository = quizRepository;
        this.assignmentRepository = assignmentRepository;
    }

    @Override
    public Enrollment createEnrollment(EnrollmentRequest request) {
        // Create User and Course references
        User user = new User();
        user.setId(request.getUserId());
        
        Course course = Course.builder()
                .courseId(request.getCourseId())
                .build();
        
        Enrollment enrollment = Enrollment.builder()
                .user(user)
                .course(course)
                .status(EnrollmentStatus.ACTIVE)
                .progressPercentage(0.0)
                .enrollmentDate(OffsetDateTime.now())
                .totalWatchTimeMinutes(0)
                .build();
        
        return enrollmentRepository.save(enrollment);
    }

   @Override
    public Enrollment updateEnrollment(UUID id, UpdateEnrollmentRequest request) {
        Optional<Enrollment> optionalEnrollment = enrollmentRepository.findById(id);
        if (optionalEnrollment.isPresent()) {
            Enrollment enrollment = optionalEnrollment.get();

            enrollment.setProgressPercentage(request.getProgressPercentage());

            if (request.getProgressPercentage() != null && request.getProgressPercentage() == 100) {
                enrollment.setStatus(EnrollmentStatus.COMPLETED); 
                enrollment.setCompletionDate(OffsetDateTime.now());
            } else {
                enrollment.setStatus(EnrollmentStatus.ACTIVE);
                enrollment.setCompletionDate(null);
            }

            enrollment.setTotalWatchTimeMinutes(request.getTotalWatchTimeMinutes());
            enrollment.setLastAccessedAt(request.getLastAccessedAt());

            return enrollmentRepository.save(enrollment);
        }
        return null;
    }


    @Override
    public void deleteEnrollment(UUID id) {
        enrollmentRepository.deleteById(id);
    }

    @Override
    public Optional<Enrollment> getEnrollmentById(UUID id) {
        return enrollmentRepository.findById(id);
    }

    @Override
    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    @Override
    public List<Enrollment> getEnrollmentsByUserId(UUID userId) {
        return enrollmentRepository.findByUserId(userId);
    }

    @Override
    public List<Enrollment> getEnrollmentsByCourseId(UUID courseId) {
        return enrollmentRepository.findByCourseId(courseId);
    }

    @Override
    public EnrollmentReportResponse getEnrollmentReport(UUID enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        
        return buildEnrollmentReport(enrollment);
    }

    @Override
    public List<EnrollmentReportResponse> getEnrollmentReportsByCourse(UUID courseId) {
        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(courseId);
        return enrollments.stream()
                .map(this::buildEnrollmentReport)
                .collect(Collectors.toList());
    }

    @Override
    public List<EnrollmentReportResponse> getEnrollmentReportsByUser(UUID userId) {
        List<Enrollment> enrollments = enrollmentRepository.findByUserId(userId);
        return enrollments.stream()
                .map(this::buildEnrollmentReport)
                .collect(Collectors.toList());
    }

    private EnrollmentReportResponse buildEnrollmentReport(Enrollment enrollment) {
        UUID courseId = enrollment.getCourse() != null ? enrollment.getCourse().getCourseId() : null;
        UUID userId = enrollment.getUser() != null ? enrollment.getUser().getId() : null;
        
        // Get quiz statistics
        // Since Quiz relates to Lecture and Lecture relates to Course, we need to find quizzes through lectures
        // For now, we'll get all quiz submissions for this enrollment
        List<QuizSubmission> userQuizSubmissions = quizSubmissionRepository.findByEnrollment(enrollment);
        
        int totalQuizzes = 0; // TODO: Calculate based on course lectures
        int completedQuizzes = userQuizSubmissions.size();
        int passedQuizzes = (int) userQuizSubmissions.stream()
                .filter(submission -> submission.getIsPassed() != null && submission.getIsPassed())
                .count();
        
        Double averageQuizScore = userQuizSubmissions.stream()
                .filter(submission -> submission.getScorePercentage() != null)
                .mapToDouble(QuizSubmission::getScorePercentage)
                .average()
                .orElse(0.0);
        
        // Get assignment statistics
        List<Assignment> courseAssignments = assignmentRepository.findByCourse_CourseId(courseId);
        List<AssignmentSubmission> userAssignmentSubmissions = assignmentSubmissionRepository.findByUser_Id(userId);
        
        int totalAssignments = courseAssignments.size();
        int submittedAssignments = userAssignmentSubmissions.size();
        int gradedAssignments = (int) userAssignmentSubmissions.stream()
                .filter(submission -> submission.getScore() != null)
                .count();
        
        Double averageAssignmentScore = userAssignmentSubmissions.stream()
                .filter(submission -> submission.getScore() != null)
                .mapToInt(AssignmentSubmission::getScore)
                .average()
                .orElse(0.0);
        
        // Get certificate information
        Optional<Certificate> certificate = certificateRepository.findByEnrollment(enrollment);
        
        return EnrollmentReportResponse.builder()
                .enrollmentId(enrollment.getId())
                .userId(userId)
                .userEmail(enrollment.getUser() != null ? enrollment.getUser().getEmail() : null)
                .courseId(courseId)
                .courseTitle(enrollment.getCourse() != null ? enrollment.getCourse().getTitle() : null)
                .enrollmentStatus(enrollment.getStatus() != null ? enrollment.getStatus().toString() : null)
                .progressPercentage(enrollment.getProgressPercentage())
                .totalWatchTimeMinutes(enrollment.getTotalWatchTimeMinutes())
                .enrollmentDate(enrollment.getEnrollmentDate())
                .completionDate(enrollment.getCompletionDate())
                .lastAccessedAt(enrollment.getLastAccessedAt())
                .totalQuizzes(totalQuizzes)
                .completedQuizzes(completedQuizzes)
                .passedQuizzes(passedQuizzes)
                .averageQuizScore(averageQuizScore)
                .totalAssignments(totalAssignments)
                .submittedAssignments(submittedAssignments)
                .gradedAssignments(gradedAssignments)
                .averageAssignmentScore(averageAssignmentScore)
                .hasCertificate(certificate.isPresent())
                .certificateNumber(certificate.map(Certificate::getCertificateNumber).orElse(null))
                .certificateIssuedDate(certificate.map(Certificate::getIssuedDate).orElse(null))
                .build();
    }
}
