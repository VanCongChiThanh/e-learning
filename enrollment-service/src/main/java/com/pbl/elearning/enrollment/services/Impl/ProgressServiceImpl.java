package com.pbl.elearning.enrollment.services.Impl;

import com.pbl.elearning.enrollment.models.Enrollment;
import com.pbl.elearning.enrollment.models.Progress;
import com.pbl.elearning.enrollment.payload.request.CreateProgressRequest;
import com.pbl.elearning.enrollment.payload.request.UpdateProgressRequest;
import com.pbl.elearning.enrollment.repository.EnrollmentRepository;
import com.pbl.elearning.enrollment.repository.ProgressRepository;
import com.pbl.elearning.enrollment.services.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProgressServiceImpl implements ProgressService {
    private final ProgressRepository progressRepository;
    private final EnrollmentRepository enrollmentRepository;
    @Autowired
    public ProgressServiceImpl(ProgressRepository progressRepository, EnrollmentRepository enrollmentRepository) {
        this.progressRepository = progressRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public Progress createProgress(CreateProgressRequest request) {
        Progress progress = new Progress();
        Enrollment enrollment = enrollmentRepository.findById(request.getEnrollmentId())
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        progress.setEnrollment(enrollment);
        progress.setLectureId(request.getLectureId());
        progress.setIsCompleted(false);
        progress.setWatchTimeMinutes(0);
        progress.setCreatedAt(OffsetDateTime.now());
        progress.setUpdatedAt(OffsetDateTime.now());
        return progressRepository.save(progress);
    }

    @Override
    public Progress updateProgress(UUID progressId, UpdateProgressRequest request) {
        Optional<Progress> optionalProgress = progressRepository.findById(progressId);
        if (optionalProgress.isPresent()) {
            Progress progress = optionalProgress.get();
            progress.setIsCompleted(request.getIsCompleted());
            progress.setWatchTimeMinutes(request.getWatchTimeMinutes());
            if (request.getIsCompleted()) {
                progress.setCompletionDate(OffsetDateTime.now());
            }
            return progressRepository.save(progress);
        }
        return null;
    }

    @Override
    public Optional<Progress> getProgressById(UUID progressId) {
        return progressRepository.findById(progressId);
    }

    @Override
    public List<Progress> getProgressByEnrollmentId(UUID enrollmentId) {
        return progressRepository.findByEnrollmentId(enrollmentId);
    }
}
