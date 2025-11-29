package com.pbl.elearning.web.endpoint.enrollment;

import com.pbl.elearning.enrollment.models.Enrollment;
import com.pbl.elearning.enrollment.payload.request.EnrollmentRequest;
import com.pbl.elearning.enrollment.payload.response.EnrollmentResponse;
import com.pbl.elearning.enrollment.payload.response.EnrollmentReportResponse;
import com.pbl.elearning.enrollment.services.EnrollmentService;
import com.pbl.elearning.user.payload.response.UserInfoResponse;
import com.pbl.elearning.course.service.impl.CourseServiceImpl;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final CourseServiceImpl courseService;

    public EnrollmentController(EnrollmentService enrollmentService, CourseServiceImpl courseService) {
        this.enrollmentService = enrollmentService;
        this.courseService = courseService;
    }

    private EnrollmentResponse toResponse(Enrollment enrollment) {
        return EnrollmentResponse.builder()
                .id(enrollment.getId())
                .user(enrollment.getUser() != null ? UserInfoResponse.toResponse(enrollment.getUser()) : null)
                .course(enrollment.getCourse() != null
                        ? courseService.getCourseInstructorById(enrollment.getCourse().getCourseId())
                        : null)
                // convert LocalDateTime to Timestamp safely (fields may be null)
                .enrollmentDate(toTimestamp(enrollment.getEnrollmentDate()))
                .completionDate(toTimestamp(enrollment.getCompletionDate()))
                .progressPercentage(enrollment.getProgressPercentage())
                .status(enrollment.getStatus())
                .totalWatchTimeMinutes(enrollment.getTotalWatchTimeMinutes())
                .lastAccessedAt(toTimestamp(enrollment.getLastAccessedAt()))
                .build();
    }

    private Timestamp toTimestamp(java.time.LocalDateTime dateTime) {
        return dateTime != null ? Timestamp.valueOf(dateTime) : null;
    }

    @PostMapping
    public ResponseEntity<EnrollmentResponse> createEnrollment(@RequestBody EnrollmentRequest request) {
        Enrollment enrollment = enrollmentService.createEnrollment(request);
        return ResponseEntity.ok(toResponse(enrollment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnrollment(@PathVariable UUID id) {
        enrollmentService.deleteEnrollment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentResponse> getEnrollmentById(@PathVariable UUID id) {
        Optional<Enrollment> enrollment = enrollmentService.getEnrollmentById(id);
        return enrollment.map(e -> ResponseEntity.ok(toResponse(e)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<EnrollmentResponse>> getAllEnrollments() {
        List<EnrollmentResponse> responses = enrollmentService.getAllEnrollments()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EnrollmentResponse>> getEnrollmentsByUserId(@PathVariable UUID userId) {
        List<EnrollmentResponse> responses = enrollmentService.getEnrollmentsByUserId(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<EnrollmentResponse>> getEnrollmentsByCourseId(@PathVariable UUID courseId) {
        System.out.println("Fetching enrollments for courseId: " + courseId);
        List<EnrollmentResponse> responses = enrollmentService.getEnrollmentsByCourseId(courseId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        System.err.println("Found " + responses);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{enrollmentId}/report")
    public ResponseEntity<EnrollmentReportResponse> getEnrollmentReport(@PathVariable UUID enrollmentId) {
        EnrollmentReportResponse report = enrollmentService.getEnrollmentReport(enrollmentId);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/reports/course/{courseId}")
    public ResponseEntity<List<EnrollmentReportResponse>> getEnrollmentReportsByCourse(@PathVariable UUID courseId) {
        List<EnrollmentReportResponse> reports = enrollmentService.getEnrollmentReportsByCourse(courseId);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/reports/user/{userId}")
    public ResponseEntity<List<EnrollmentReportResponse>> getEnrollmentReportsByUser(@PathVariable UUID userId) {
        List<EnrollmentReportResponse> reports = enrollmentService.getEnrollmentReportsByUser(userId);
        return ResponseEntity.ok(reports);
    }
    
}
