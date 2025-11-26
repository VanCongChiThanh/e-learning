package com.pbl.elearning.enrollment.services;

import com.pbl.elearning.enrollment.models.Progress;
import com.pbl.elearning.enrollment.payload.request.CreateProgressRequest;
import com.pbl.elearning.enrollment.payload.request.UpdateLectureProgressRequest;
import com.pbl.elearning.enrollment.payload.response.EnrollmentProgressSummaryResponse;
import com.pbl.elearning.enrollment.payload.response.LectureProgressUpdateResponse;
import com.pbl.elearning.enrollment.payload.response.ProgressResponse;
import com.pbl.elearning.enrollment.payload.response.RecentLearningResponse;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProgressService {
    Double calculateProgressPercentage(Progress progress);
    Progress createProgress(CreateProgressRequest request);
    // Progress updateProgress(UUID progressId, UpdateProgressRequest request);
    Optional<Progress> getProgressById(UUID progressId);

    List<Progress> getProgressByEnrollmentId(UUID enrollmentId);
    List<Progress> getProgressByLectureId(UUID lectureId);
    
    // Enhanced response methods
    ProgressResponse getProgressResponseById(UUID progressId);
    List<ProgressResponse> getProgressResponsesByEnrollmentId(UUID enrollmentId);
    
    // New progress tracking methods
    LectureProgressUpdateResponse updateLectureProgress(
            UpdateLectureProgressRequest request);
    
    // Helper methods
    void updateEnrollmentProgress(UUID enrollmentId);
    
    // Batch update enrollment progress for multiple enrollments
    void updateEnrollmentProgressBatch(List<UUID> enrollmentIds);
    
    // Get detailed enrollment progress summary
    EnrollmentProgressSummaryResponse getEnrollmentProgressSummary(UUID enrollmentId);
    
    // Get recent learning session info
    RecentLearningResponse getRecentLearningByEnrollmentId(UUID enrollmentId);
}
