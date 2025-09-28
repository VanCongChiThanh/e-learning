package com.pbl.elearning.web.endpoint.enrollment;

import com.pbl.elearning.enrollment.models.Progress;
import com.pbl.elearning.enrollment.payload.request.CreateProgressRequest;
import com.pbl.elearning.enrollment.payload.request.UpdateProgressRequest;
import com.pbl.elearning.enrollment.payload.response.ProgressResponse;
import com.pbl.elearning.enrollment.services.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/v1/progress")
public class ProgressController {
    private final ProgressService progressService;

    @Autowired
    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    private ProgressResponse toResponse(Progress progress) {
        return ProgressResponse.builder()
                .id(progress.getId())
                .enrollmentId(progress.getEnrollment().getId())
                .lectureId(progress.getLectureId())
                .isCompleted(progress.getIsCompleted())
                .watchTimeMinutes(progress.getWatchTimeMinutes())
                .completionDate(progress.getCompletionDate())
                .createdAt(progress.getCreatedAt())
                .updatedAt(progress.getUpdatedAt())
                .build();
    }

    @PostMapping
    public ResponseEntity<ProgressResponse> createProgress(@RequestBody CreateProgressRequest request){
        Progress progress = progressService.createProgress(request);
        return ResponseEntity.ok(toResponse(progress));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProgressResponse> updateProgress(@PathVariable UUID id, @RequestBody UpdateProgressRequest request){
        Progress progress = progressService.updateProgress(id, request);
        if (progress != null) {
            return ResponseEntity.ok(toResponse(progress));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProgressResponse> getProgressById(@PathVariable UUID id){
        Optional<Progress> progress = progressService.getProgressById(id);
        return progress.map(value -> ResponseEntity.ok(toResponse(value))).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/enrollment/{id}")
    public ResponseEntity<List<ProgressResponse>> getProgressByEnrollmentId(@PathVariable UUID id){
        List<Progress> progress = progressService.getProgressByEnrollmentId(id);
        if (progress.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<ProgressResponse> responses = progress.stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }
}
