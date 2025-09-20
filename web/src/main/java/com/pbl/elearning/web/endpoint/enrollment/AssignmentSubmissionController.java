package com.pbl.elearning.web.endpoint.enrollment;

import com.pbl.elearning.enrollment.payload.request.AssignmentSubmissionRequest;
import com.pbl.elearning.enrollment.payload.response.AssignmentSubmissionResponse;
import com.pbl.elearning.enrollment.services.AssignmentSubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/assignment-submissions")
@RequiredArgsConstructor
public class AssignmentSubmissionController {

    private final AssignmentSubmissionService service;

    @PostMapping
    public ResponseEntity<AssignmentSubmissionResponse> create(@RequestBody AssignmentSubmissionRequest request) {
        return ResponseEntity.ok(service.createSubmission(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssignmentSubmissionResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getSubmissionById(id));
    }

    @GetMapping("/assignment/{assignmentId}")
    public ResponseEntity<List<AssignmentSubmissionResponse>> getByAssignment(@PathVariable UUID assignmentId) {
        return ResponseEntity.ok(service.getSubmissionsByAssignment(assignmentId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AssignmentSubmissionResponse>> getByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(service.getSubmissionsByUser(userId));
    }

    @PutMapping("/{id}/grade")
    public ResponseEntity<AssignmentSubmissionResponse> gradeSubmission(@PathVariable UUID id,
                                                                        @RequestParam Integer score,
                                                                        @RequestParam String feedback,
                                                                        @RequestParam UUID gradedBy) {
        return ResponseEntity.ok(service.gradeSubmission(id, score, feedback, gradedBy));
    }
}