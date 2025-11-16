package com.pbl.elearning.enrollment.services.Impl;

import com.pbl.elearning.enrollment.Enum.EnrollmentStatus;
import com.pbl.elearning.enrollment.models.*;
import com.pbl.elearning.enrollment.payload.request.EnrollmentRequest;
import com.pbl.elearning.enrollment.payload.request.UpdateEnrollmentRequest;
import com.pbl.elearning.enrollment.payload.response.EnrollmentReportResponse;
import com.pbl.elearning.enrollment.repository.*;
import com.pbl.elearning.enrollment.services.EnrollmentService;
import com.pbl.elearning.user.domain.UserInfo;
import com.pbl.elearning.course.domain.Course;
import com.pbl.elearning.course.domain.Lecture;
import com.pbl.elearning.course.repository.LectureRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;  // để inject eventPublisher
@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final QuizSubmissionRepository quizSubmissionRepository;
    private final AssignmentSubmissionRepository assignmentSubmissionRepository;
    private final CertificateRepository certificateRepository;
    private final QuizRepository quizRepository;
    private final AssignmentRepository assignmentRepository;
    private final LectureRepository lectureRepository;
    private final ProgressRepository progressRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    public EnrollmentServiceImpl(
            EnrollmentRepository enrollmentRepository,
            QuizSubmissionRepository quizSubmissionRepository,
            AssignmentSubmissionRepository assignmentSubmissionRepository,
            CertificateRepository certificateRepository,
            QuizRepository quizRepository,
            AssignmentRepository assignmentRepository,
            LectureRepository lectureRepository, 
                    ProgressRepository progressRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.quizSubmissionRepository = quizSubmissionRepository;
        this.assignmentSubmissionRepository = assignmentSubmissionRepository;
        this.certificateRepository = certificateRepository;
        this.quizRepository = quizRepository;
        this.assignmentRepository = assignmentRepository;
        this.lectureRepository = lectureRepository;
        this.progressRepository = progressRepository;
    }

    @Transactional 
    @Override
    public Enrollment createEnrollment(EnrollmentRequest request) {
        // User user = new User();
        UserInfo user = new UserInfo();
        user.setUserId(request.getUserId());
        
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
        
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        
        List<Lecture> lecturesOfCourse = lectureRepository.findBySection_Course_CourseId(request.getCourseId());
        
        if (lecturesOfCourse != null && !lecturesOfCourse.isEmpty()) {
            List<Progress> newProgresses = new ArrayList<>();
            OffsetDateTime now = OffsetDateTime.now();
            
            for (Lecture lecture : lecturesOfCourse) {
                Progress progress = Progress.builder()
                        .enrollment(savedEnrollment) 
                        .lecture(lecture)            
                        .isCompleted(false)
                        .watchTimeMinutes(0)
                        .createdAt(now)
                        .updatedAt(now)
                        .build();
                newProgresses.add(progress);
            }
            
            progressRepository.saveAll(newProgresses);
        }
        
        return savedEnrollment;
    }
@Override
public Enrollment updateEnrollment(UUID id, UpdateEnrollmentRequest request) {
    Optional<Enrollment> optionalEnrollment = enrollmentRepository.findById(id);
    if (!optionalEnrollment.isPresent()) {
        return null;
    }

    Enrollment enrollment = optionalEnrollment.get();

    // 1️⃣ Cập nhật tiến trình và trạng thái
    enrollment.setProgressPercentage(request.getProgressPercentage());
    boolean justCompleted = false;

    if (request.getProgressPercentage() != null && request.getProgressPercentage() == 100) {
        if (enrollment.getStatus() != EnrollmentStatus.COMPLETED) {
            justCompleted = true; // chỉ trigger khi vừa đạt 100%
        }
        enrollment.setStatus(EnrollmentStatus.COMPLETED);
        enrollment.setCompletionDate(OffsetDateTime.now());
    } else {
        enrollment.setStatus(EnrollmentStatus.ACTIVE);
        enrollment.setCompletionDate(null);
    }

    enrollment.setTotalWatchTimeMinutes(request.getTotalWatchTimeMinutes());
    enrollment.setLastAccessedAt(request.getLastAccessedAt());

    // 2️⃣ Lưu enrollment
    Enrollment saved = enrollmentRepository.save(enrollment);

    // 3️⃣ Phát sinh event nếu vừa hoàn thành
    if (justCompleted) {
        eventPublisher.publishEvent(new EnrollmentCompletedEvent(enrollment.getId()));
    }

    return saved;
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
        return enrollmentRepository.findByUser_UserId(userId);
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
        List<Enrollment> enrollments = enrollmentRepository.findByUser_UserId(userId);
        return enrollments.stream()
                .map(this::buildEnrollmentReport)
                .collect(Collectors.toList());
    }

    private EnrollmentReportResponse buildEnrollmentReport(Enrollment enrollment) {
        UUID courseId = enrollment.getCourse() != null ? enrollment.getCourse().getCourseId() : null;
        UUID userId = enrollment.getUser() != null ? enrollment.getUser().getUserId() : null;
        
        List<QuizSubmission> userQuizSubmissions = quizSubmissionRepository.findByEnrollment(enrollment);
        
        int totalQuizzes = 0; 
        int completedQuizzes = userQuizSubmissions.size();
        int passedQuizzes = (int) userQuizSubmissions.stream()
                .filter(submission -> submission.getIsPassed() != null && submission.getIsPassed())
                .count();
        
        Double averageQuizScore = userQuizSubmissions.stream()
                .filter(submission -> submission.getScorePercentage() != null)
                .mapToDouble(QuizSubmission::getScorePercentage)
                .average()
                .orElse(0.0);
        
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
        
        Optional<Certificate> certificate = certificateRepository.findByEnrollment(enrollment);
        
        return EnrollmentReportResponse.builder()
                .enrollmentId(enrollment.getId())
                .userId(userId)
                .userEmail(enrollment.getUser() != null ? enrollment.getUser().getEmail() : null)
                .userFullName(enrollment.getUser().getFirstName() + " " + enrollment.getUser().getLastName())
                .avatar(enrollment.getUser().getAvatar())
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
