package com.pbl.elearning.web.endpoint.enrollment;

import com.pbl.elearning.enrollment.models.Enrollment;
import com.pbl.elearning.enrollment.payload.request.EnrollmentRequest;
import com.pbl.elearning.enrollment.payload.request.UpdateEnrollmentRequest;
import com.pbl.elearning.enrollment.payload.response.EnrollmentResponse;
import com.pbl.elearning.enrollment.services.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @Autowired
    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    // Hàm helper map Enrollment -> EnrollmentResponse
    private EnrollmentResponse toResponse(Enrollment enrollment) {
        return EnrollmentResponse.builder()
                .id(enrollment.getId())
                .userId(enrollment.getUserId())
                .courseId(enrollment.getCourseId())
                .enrollmentDate(enrollment.getEnrollmentDate())
                .completionDate(enrollment.getCompletionDate())
                .progressPercentage(enrollment.getProgressPercentage())
                .status(enrollment.getStatus())
                .totalWatchTimeMinutes(enrollment.getTotalWatchTimeMinutes())
                .lastAccessedAt(enrollment.getLastAccessedAt())
                .createdAt(enrollment.getCreatedAt())
                .updatedAt(enrollment.getUpdatedAt())
                .build();
    }

    // Tạo Enrollment mới
    @PostMapping
    public ResponseEntity<EnrollmentResponse> createEnrollment(@RequestBody EnrollmentRequest request) {
        Enrollment enrollment = enrollmentService.createEnrollment(request);
        return ResponseEntity.ok(toResponse(enrollment));
    }

    // Cập nhật Enrollment theo ID
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

    // Xóa Enrollment theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEnrollment(@PathVariable UUID id) {
        enrollmentService.deleteEnrollment(id);
        return ResponseEntity.noContent().build();
    }

    // Lấy Enrollment theo ID
    @GetMapping("/{id}")
    public ResponseEntity<EnrollmentResponse> getEnrollmentById(@PathVariable UUID id) {
        Optional<Enrollment> enrollment = enrollmentService.getEnrollmentById(id);
        return enrollment.map(e -> ResponseEntity.ok(toResponse(e)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Lấy tất cả Enrollment
    @GetMapping
    public ResponseEntity<List<EnrollmentResponse>> getAllEnrollments() {
        List<EnrollmentResponse> responses = enrollmentService.getAllEnrollments()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // Lấy Enrollment theo userId
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<EnrollmentResponse>> getEnrollmentsByUserId(@PathVariable UUID userId) {
        List<EnrollmentResponse> responses = enrollmentService.getEnrollmentsByUserId(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // Lấy Enrollment theo courseId
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<EnrollmentResponse>> getEnrollmentsByCourseId(@PathVariable UUID courseId) {
        List<EnrollmentResponse> responses = enrollmentService.getEnrollmentsByCourseId(courseId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}
