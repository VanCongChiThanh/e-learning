package com.pbl.elearning.enrollment.services.Impl;

import com.pbl.elearning.enrollment.models.Enrollment;
import com.pbl.elearning.enrollment.models.Progress;
import com.pbl.elearning.enrollment.payload.request.CreateProgressRequest;
import com.pbl.elearning.enrollment.payload.request.UpdateProgressRequest;
import com.pbl.elearning.enrollment.payload.response.ProgressResponse;
import com.pbl.elearning.enrollment.repository.EnrollmentRepository;
import com.pbl.elearning.enrollment.repository.ProgressRepository;
import com.pbl.elearning.enrollment.services.ProgressService;
import com.pbl.elearning.course.domain.Lecture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProgressServiceImpl implements ProgressService {
    private final ProgressRepository progressRepository;
    private final EnrollmentRepository enrollmentRepository;
    
    @Autowired
    public ProgressServiceImpl(ProgressRepository progressRepository, EnrollmentRepository enrollmentRepository) {
        this.progressRepository = progressRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    private ProgressResponse mapToResponse(Progress progress) {
        return ProgressResponse.builder()
                .id(progress.getId())
                .enrollmentId(progress.getEnrollment() != null ? progress.getEnrollment().getId() : null)
                .lectureId(progress.getLecture() != null ? progress.getLecture().getLectureId() : null)
                .isCompleted(progress.getIsCompleted())
                .watchTimeMinutes(progress.getWatchTimeMinutes())
                .completionDate(progress.getCompletionDate())
                .createdAt(progress.getCreatedAt())
                .updatedAt(progress.getUpdatedAt())
                .build();
    }
    
    @Override
    public Double calculateProgressPercentage(Progress progress) {
        if (progress.getIsCompleted()) {
            return 100.0;
        }
        if (progress.getLecture() != null && progress.getLecture().getDuration() != null && 
            progress.getWatchTimeMinutes() != null) {
            return Math.min(100.0, (progress.getWatchTimeMinutes().doubleValue() / 
                                  progress.getLecture().getDuration().doubleValue()) * 100);
        }
        return 0.0;
    }

    @Override
    public Progress createProgress(CreateProgressRequest request) {
        Progress progress = new Progress();
        Enrollment enrollment = enrollmentRepository.findById(request.getEnrollmentId())
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        
        Lecture lecture = Lecture.builder()
                .lectureId(request.getLectureId())
                .build();
        
        progress.setEnrollment(enrollment);
        progress.setLecture(lecture);
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
        return progressRepository.findByEnrollment_Id(enrollmentId);
    }

    @Override
    public List<Progress> getProgressByLectureId(UUID lectureId) {
        return progressRepository.findByLecture_LectureId(lectureId);
    }
    
    @Override
    public ProgressResponse getProgressResponseById(UUID progressId) {
        Optional<Progress> progress = progressRepository.findById(progressId);
        return progress.map(this::mapToResponse).orElse(null);
    }
    
    @Override
    public List<ProgressResponse> getProgressResponsesByEnrollmentId(UUID enrollmentId) {
        List<Progress> progressList = progressRepository.findByEnrollment_Id(enrollmentId);
        return progressList.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
}
