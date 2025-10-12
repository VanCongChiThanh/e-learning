package com.pbl.elearning.web.endpoint.enrollment;

import com.pbl.elearning.enrollment.payload.request.AssignmentRequest;
import com.pbl.elearning.enrollment.payload.response.AssignmentResponse;
import com.pbl.elearning.enrollment.services.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService service;

    @PostMapping
    public ResponseEntity<AssignmentResponse> create(@RequestBody AssignmentRequest request) {
        System.out.println("request " + request);
        return ResponseEntity.ok(service.createAssignment(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssignmentResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getAssignmentById(id));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<AssignmentResponse>> getByCourse(@PathVariable UUID courseId) {
        return ResponseEntity.ok(service.getAssignmentsByCourseId(courseId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssignmentResponse> update(@PathVariable UUID id,
                                                     @RequestBody AssignmentRequest request) {
        return ResponseEntity.ok(service.updateAssignment(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteAssignment(id);
        return ResponseEntity.noContent().build();
    }
}