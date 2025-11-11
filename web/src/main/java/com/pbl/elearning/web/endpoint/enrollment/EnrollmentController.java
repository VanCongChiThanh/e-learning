package com.pbl.elearning.web.endpoint.enrollment;

import com.pbl.elearning.enrollment.models.Enrollment;
import com.pbl.elearning.enrollment.payload.request.EnrollmentRequest;
import com.pbl.elearning.enrollment.payload.request.UpdateEnrollmentRequest;
import com.pbl.elearning.enrollment.payload.response.EnrollmentResponse;
import com.pbl.elearning.enrollment.payload.response.EnrollmentReportResponse;
import com.pbl.elearning.enrollment.services.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @Autowired
    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }
    private EnrollmentResponse toResponse(Enrollment enrollment) {
        return EnrollmentResponse.builder()
                .id(enrollment.getId())
                .userId(enrollment.getUser() != null ? enrollment.getUser().getId() : null)
                .courseId(enrollment.getCourse() != null ? enrollment.getCourse().getCourseId() : null)
                .enrollmentDate(enrollment.getEnrollmentDate())
                .completionDate(enrollment.getCompletionDate())
                .progressPercentage(enrollment.getProgressPercentage())
                .status(enrollment.getStatus())
                .totalWatchTimeMinutes(enrollment.getTotalWatchTimeMinutes())
                .lastAccessedAt(enrollment.getLastAccessedAt())
                .build();
    }

    @PostMapping
    public ResponseEntity<EnrollmentResponse> createEnrollment(@RequestBody EnrollmentRequest request) {
        Enrollment enrollment = enrollmentService.createEnrollment(request);
        return ResponseEntity.ok(toResponse(enrollment));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnrollmentResponse> updateEnrollment(
            @PathVariable UUID id,
            @RequestBody UpdateEnrollmentRequest request) {
        Enrollment updated = enrollmentService.updateEnrollment(id, request);
        if (updated != null) {
            return ResponseEntity.ok(toResponse(updated));
        }
        return ResponseEntity.notFound().build();
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
        System.out.println("Fetching enrollments for userId: " + userId);
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
        System.err.println("Found " + responses );
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
