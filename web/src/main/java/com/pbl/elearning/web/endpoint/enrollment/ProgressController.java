package com.pbl.elearning.web.endpoint.enrollment;

import com.pbl.elearning.enrollment.models.Progress;
import com.pbl.elearning.enrollment.payload.request.CreateProgressRequest;
import com.pbl.elearning.enrollment.payload.request.UpdateLectureProgressRequest;
import com.pbl.elearning.enrollment.payload.response.ProgressResponse;
import com.pbl.elearning.enrollment.payload.response.RecentLearningResponse;
import com.pbl.elearning.enrollment.payload.response.LectureProgressUpdateResponse;
import com.pbl.elearning.enrollment.payload.response.EnrollmentProgressSummaryResponse;
import com.pbl.elearning.enrollment.services.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
                .lectureId(progress.getLecture() != null ? progress.getLecture().getLectureId() : null)
                .isCompleted(progress.getIsCompleted())
                .lastViewedAt(progress.getLastViewedAt())
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
    @GetMapping("/lecture/{id}")
    public ResponseEntity<List<Map<String, Object>>> getProgressByLectureId(@PathVariable UUID id) {
        List<Progress> progressList = progressService.getProgressByLectureId(id);

        if (progressList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Map<String, Object>> result = progressList.stream()
            .map(progress -> {
                Map<String, Object> map = new LinkedHashMap<>();

                map.put("id", progress.getId());
                map.put("enrollmentId", progress.getEnrollment().getId());
                map.put("lectureId", progress.getLecture().getLectureId());
                map.put("isCompleted", progress.getIsCompleted());
                map.put("lastViewedAt", progress.getLastViewedAt()); 
                map.put("completionDate", progress.getCompletionDate());
                map.put("createdAt", progress.getCreatedAt());
                map.put("updatedAt", progress.getUpdatedAt());

                map.put("sectionId", progress.getLecture().getSection().getSectionId());
                map.put("courseId", progress.getLecture().getSection().getCourse().getCourseId());

                return map;
            })
            .toList();

        return ResponseEntity.ok(result);
    }

    
    @GetMapping("/{id}/response")
    public ResponseEntity<ProgressResponse> getProgressResponseById(@PathVariable UUID id) {
        ProgressResponse response = progressService.getProgressResponseById(id);
        return response != null ? ResponseEntity.ok(response) : ResponseEntity.notFound().build();
    }
    
    @GetMapping("/enrollment/{id}/responses")
    public ResponseEntity<List<ProgressResponse>> getProgressResponsesByEnrollmentId(@PathVariable UUID id) {
        List<ProgressResponse> responses = progressService.getProgressResponsesByEnrollmentId(id);
        return ResponseEntity.ok(responses);
    }
    @PutMapping("/lecture-progress")
    public ResponseEntity<LectureProgressUpdateResponse> updateLectureProgress(
            @Valid @RequestBody UpdateLectureProgressRequest request) {
            LectureProgressUpdateResponse response = progressService.updateLectureProgress(request);
            return ResponseEntity.ok(response);
    }
    
    @PutMapping("/enrollment/{enrollmentId}/recalculate")
    public ResponseEntity<Void> recalculateEnrollmentProgress(@PathVariable UUID enrollmentId) {
        try {
            progressService.updateEnrollmentProgress(enrollmentId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/enrollment/batch-recalculate")
    public ResponseEntity<Void> recalculateEnrollmentProgressBatch(@RequestBody List<UUID> enrollmentIds) {
        try {
            progressService.updateEnrollmentProgressBatch(enrollmentIds);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/enrollment/{enrollmentId}/summary")
    public ResponseEntity<EnrollmentProgressSummaryResponse> getEnrollmentProgressSummary(
            @PathVariable UUID enrollmentId) {
        try {
            EnrollmentProgressSummaryResponse response = progressService.getEnrollmentProgressSummary(enrollmentId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/enrollment/{enrollmentId}/recent-learning")
    public ResponseEntity<RecentLearningResponse> getRecentLearning(@PathVariable UUID enrollmentId) {
        try {
            RecentLearningResponse response = progressService.getRecentLearningByEnrollmentId(enrollmentId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
