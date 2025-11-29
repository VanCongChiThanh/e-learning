package com.pbl.elearning.enrollment.services.Impl;

import com.pbl.elearning.enrollment.models.Enrollment;
import com.pbl.elearning.enrollment.models.Progress;
import com.pbl.elearning.enrollment.models.EnrollmentCompletedEvent;
import com.pbl.elearning.enrollment.payload.request.CreateProgressRequest;
import com.pbl.elearning.enrollment.payload.request.UpdateLectureProgressRequest;
import com.pbl.elearning.enrollment.payload.response.ProgressResponse;
import com.pbl.elearning.enrollment.payload.response.EnrollmentProgressSummaryResponse;
import com.pbl.elearning.enrollment.payload.response.LectureProgressUpdateResponse;
import com.pbl.elearning.enrollment.repository.EnrollmentRepository;
import com.pbl.elearning.enrollment.repository.ProgressRepository;
import com.pbl.elearning.enrollment.services.ProgressService;
import com.pbl.elearning.enrollment.enums.EnrollmentStatus;
import com.pbl.elearning.course.domain.Course;
import com.pbl.elearning.course.domain.Lecture;
import com.pbl.elearning.course.domain.Section;
import com.pbl.elearning.course.repository.LectureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.pbl.elearning.enrollment.payload.response.RecentLearningResponse;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProgressServiceImpl implements ProgressService {
    private final ProgressRepository progressRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LectureRepository lectureRepository;
    private final ApplicationEventPublisher eventPublisher;
    
    @Autowired
    public ProgressServiceImpl(ProgressRepository progressRepository, 
                              EnrollmentRepository enrollmentRepository,
                              LectureRepository lectureRepository,
                              ApplicationEventPublisher eventPublisher) {
        this.progressRepository = progressRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.lectureRepository = lectureRepository;
        this.eventPublisher = eventPublisher;
    }

    private ProgressResponse mapToResponse(Progress progress) {
        return ProgressResponse.builder()
                .id(progress.getId())
                .enrollmentId(progress.getEnrollment() != null ? progress.getEnrollment().getId() : null)
                .lectureId(progress.getLecture() != null ? progress.getLecture().getLectureId() : null)
                .isCompleted(progress.getIsCompleted())
                .lastViewedAt(progress.getLastViewedAt())
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
            progress.getLastViewedAt() != null) {
            Double totalDurationMinutes = progress.getLecture().getDuration().doubleValue();
            System.out.println("Total Duration Minutes: " + progress.getLastViewedAt().toSecondOfDay());
            return Math.min(100.0, (progress.getLastViewedAt().toSecondOfDay() / totalDurationMinutes) * 100.0);
        }
        return 0.0;
    }

    @Override
    @Transactional
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
        progress.setLastViewedAt(null);
        progress.setCreatedAt(OffsetDateTime.now());
        progress.setUpdatedAt(OffsetDateTime.now());

        Progress savedProgress = progressRepository.save(progress);

        updateEnrollmentProgress(savedProgress.getEnrollment().getId());

        return savedProgress;
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
    
    @Override
    @Transactional
    public LectureProgressUpdateResponse updateLectureProgress(UpdateLectureProgressRequest request) {
        Optional<Progress> existingProgress = progressRepository
                .findByEnrollment_User_UserIdAndLecture_LectureId(request.getUserId(), request.getLectureId());
        System.out.println("Existing Progress: " + existingProgress);
        Progress progress;
        if (existingProgress.isPresent()) {
            progress = existingProgress.get();
        } else {
            throw new RuntimeException("Progress not found for the given user and lecture");
        }
        
        Lecture lecture = progress.getLecture();
        if (lecture.getDuration() == null) {
            throw new RuntimeException("Lecture duration is not set");
        }
        Integer totalDurationSeconds = lecture.getDuration();
        Double totalDurationMinutes = totalDurationSeconds / 60.0;
        Double newWatchTimeSecondsFromRequest = (double) request.getLastViewAt().toSecondOfDay(); 
        Double currentWatchTimeSeconds = progress.getLastViewedAt() != null 
            ? (double) progress.getLastViewedAt().toSecondOfDay() 
            : 0.0;
        Double finalWatchTimeSeconds = Math.max(
            newWatchTimeSecondsFromRequest,
            currentWatchTimeSeconds
        );
        finalWatchTimeSeconds = Math.min(finalWatchTimeSeconds, (double) totalDurationSeconds);   
        Double finalWatchTimeMinutes = finalWatchTimeSeconds / 60.0;
        Double lectureProgressPercentage = (finalWatchTimeMinutes / totalDurationMinutes) * 100.0;
        long totalSeconds = finalWatchTimeSeconds.longValue();
        progress.setLastViewedAt(LocalTime.ofSecondOfDay(totalSeconds));
        Boolean isLectureCompleted = lectureProgressPercentage >= 95.0;
        progress.setIsCompleted(isLectureCompleted);
        progress.setCompletionDate(isLectureCompleted ? OffsetDateTime.now() : null);
        progress.setUpdatedAt(OffsetDateTime.now());
        
        if (isLectureCompleted && progress.getCompletionDate() == null) {
            progress.setCompletionDate(OffsetDateTime.now());
        }
        Progress savedProgress = progressRepository.save(progress);
        updateEnrollmentProgress(savedProgress.getEnrollment().getId());
        
        Enrollment enrollment = savedProgress.getEnrollment();
        UUID courseId = enrollment.getCourse().getCourseId();
        
        Long completedLectures = progressRepository.countByEnrollment_Course_CourseIdAndIsCompleted(courseId, true);
        Integer totalLectures = lectureRepository.countBySection_Course_CourseId(courseId);
        
        return LectureProgressUpdateResponse.builder()
                .progressId(savedProgress.getId())
                .enrollmentId(savedProgress.getEnrollment().getId())
                .lectureId(savedProgress.getLecture().getLectureId())
                .lastViewedAt(savedProgress.getLastViewedAt())
                .lectureProgressPercentage(lectureProgressPercentage)
                .isLectureCompleted(savedProgress.getIsCompleted())
                .updatedAt(savedProgress.getUpdatedAt())
                .enrollmentProgressPercentage(enrollment.getProgressPercentage())
                .isEnrollmentCompleted(enrollment.getStatus() == EnrollmentStatus.COMPLETED)
                .completedLecturesCount(completedLectures.intValue())
                .totalLecturesCount(totalLectures)
                .build();
    }
    @Override
    @Transactional
    public void updateEnrollmentProgress(UUID enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        UUID courseId = enrollment.getCourse().getCourseId();
        
        Long completedLectures = progressRepository.countByEnrollment_Course_CourseIdAndIsCompleted(courseId, true);
        Integer totalLectures = lectureRepository.countBySection_Course_CourseId(courseId);
        
        List<Progress> allProgress = progressRepository.findByEnrollment_Id(enrollmentId);
        Double totalWatchTime = allProgress.stream()
                .mapToDouble(p -> p.getLastViewedAt() != null ? p.getLastViewedAt().toSecondOfDay() : 0.0)
                .sum();
        
        if (totalLectures > 0) {
            Double enrollmentProgressPercentage = (completedLectures.doubleValue() / totalLectures.doubleValue()) * 100.0;
            enrollment.setProgressPercentage(enrollmentProgressPercentage);
            
            enrollment.setTotalWatchTimeMinutes(totalWatchTime);
            
            enrollment.setLastAccessedAt(java.time.LocalDateTime.now());
            
            boolean justCompleted = false;
            
            if (enrollmentProgressPercentage >= 90.0) {
                enrollment.setStatus(EnrollmentStatus.COMPLETED);
                justCompleted = true;
                if (enrollment.getCompletionDate() == null) {
                    enrollment.setCompletionDate(java.time.LocalDateTime.now());
                }
            } else if (enrollmentProgressPercentage > 0.0 && enrollment.getStatus() != EnrollmentStatus.COMPLETED) {
                enrollment.setStatus(EnrollmentStatus.ACTIVE);
            }
            
            enrollmentRepository.save(enrollment);
            
            if (justCompleted) {
                eventPublisher.publishEvent(new EnrollmentCompletedEvent(enrollment.getId()));
            }
        }
    }
    
    @Override
    @Transactional
    public void updateEnrollmentProgressBatch(List<UUID> enrollmentIds) {
        for (UUID enrollmentId : enrollmentIds) {
            try {
                updateEnrollmentProgress(enrollmentId);
            } catch (Exception e) {
                System.err.println("Error updating enrollment progress for ID " + enrollmentId + ": " + e.getMessage());
            }
        }
    }
    
    @Override
    public EnrollmentProgressSummaryResponse getEnrollmentProgressSummary(UUID enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        
        UUID courseId = enrollment.getCourse().getCourseId();
        
        // Get progress statistics
        Long completedLectures = progressRepository.countByEnrollment_Course_CourseIdAndIsCompleted(courseId, true);
        Integer totalLectures = lectureRepository.countBySection_Course_CourseId(courseId);
        
        // Get total watch time
        List<Progress> allProgress = progressRepository.findByEnrollment_Id(enrollmentId);
        Double totalWatchTime = allProgress.stream()
                .mapToDouble(p -> p.getLastViewedAt() != null ? p.getLastViewedAt().toSecondOfDay() : 0.0)
                .sum();
        
        // Get last accessed lecture
        Optional<Progress> lastProgress = allProgress.stream()
                .filter(p -> p.getUpdatedAt() != null)
                .max((p1, p2) -> p1.getUpdatedAt().compareTo(p2.getUpdatedAt()));
        
        return EnrollmentProgressSummaryResponse.builder()
                .enrollmentId(enrollment.getId())
                .userId(enrollment.getUser().getId())
                .courseId(courseId)
                .courseTitle(enrollment.getCourse().getTitle())
                .progressPercentage(enrollment.getProgressPercentage())
                .completedLecturesCount(completedLectures.intValue())
                .totalLecturesCount(totalLectures)
                .totalWatchTimeMinutes(totalWatchTime)
                .enrollmentStatus(enrollment.getStatus().toString())
                .enrollmentDate(enrollment.getEnrollmentDate())
                .completionDate(enrollment.getCompletionDate())
                .lastAccessedAt(enrollment.getLastAccessedAt())
                .lastViewedLectureId(lastProgress.map(p -> p.getLecture().getLectureId()).orElse(null))
                .lastViewedLectureTitle(lastProgress.map(p -> p.getLecture().getTitle()).orElse(null))
                .build();
    }
    
    @Override
    public RecentLearningResponse getRecentLearningByEnrollmentId(UUID enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        
        List<Progress> allProgress = progressRepository.findByEnrollment_Id(enrollmentId);
        
        Optional<Progress> recentProgress = allProgress.stream()
                .filter(p -> p.getUpdatedAt() != null)
                .max((p1, p2) -> p1.getUpdatedAt().compareTo(p2.getUpdatedAt()));
        
        if (!recentProgress.isPresent()) {
            return getFirstLectureInfo(enrollment);
        }
        
        Progress progress = recentProgress.get();
        Lecture lecture = progress.getLecture();
        Section section = lecture.getSection();
        Course course = enrollment.getCourse();
        
        Double lectureProgressPercentage = calculateProgressPercentage(progress);
        
        String currentStatus;
        String recommendedAction;
        
        if (progress.getIsCompleted() != null && progress.getIsCompleted()) {
            currentStatus = "COMPLETED";
            recommendedAction = "Move to next lecture";
        } else if (progress.getLastViewedAt() != null && progress.getLastViewedAt().toSecondOfDay() > 0) {
            currentStatus = "CONTINUE_WATCHING";
            recommendedAction = String.format("Continue from %02d:%02d", 
                progress.getLastViewedAt().getHour(), 
                progress.getLastViewedAt().getMinute());
        } else {
            currentStatus = "START_NEW";
            recommendedAction = "Start this lecture";
        }
        
        return RecentLearningResponse.builder()
                .enrollmentId(enrollment.getId())
                .enrollmentStatus(enrollment.getStatus().toString())
                .enrollmentProgressPercentage(enrollment.getProgressPercentage())
                .lastAccessedAt(enrollment.getLastAccessedAt() != null ? 
                    enrollment.getLastAccessedAt().atOffset(java.time.ZoneOffset.UTC) : null)
                
                // Course info
                .courseId(course.getCourseId())
                .courseTitle(course.getTitle())
                .courseDescription(course.getDescription())
                .courseImage(course.getImage())
                
                // Section info
                .sectionId(section.getSectionId())
                .sectionTitle(section.getTitle())
                .position(section.getPosition())
                
                // Recent lecture info
                .lectureId(lecture.getLectureId())
                .lectureTitle(lecture.getTitle())
                .lectureVideoUrl(lecture.getSourceUrl())
                .lectureDuration(lecture.getDuration())
                .lectureOrder(lecture.getPosition())
                
                // Progress info
                .progressId(progress.getId())
                .isLectureCompleted(progress.getIsCompleted())
                .lastViewedAt(progress.getLastViewedAt())
                .progressUpdatedAt(progress.getUpdatedAt())
                .lectureProgressPercentage(lectureProgressPercentage)
                
                // Learning session info
                .currentLearningStatus(currentStatus)
                .recommendedAction(recommendedAction)
                .build();
    }
    
    private RecentLearningResponse getFirstLectureInfo(Enrollment enrollment) {
        Course course = enrollment.getCourse();
        
        Optional<Section> firstSection = course.getSections().stream()
                .min((s1, s2) -> Integer.compare(s1.getPosition(), s2.getPosition()));
        
        if (!firstSection.isPresent()) {
            throw new RuntimeException("Course has no sections");
        }
        
        // Tìm lecture đầu tiên trong section đầu tiên
        Optional<Lecture> firstLecture = firstSection.get().getLectures().stream()
                .min((l1, l2) -> Integer.compare(l1.getPosition(), l2.getPosition()));
        
        if (!firstLecture.isPresent()) {
            throw new RuntimeException("Course has no lectures");
        }
        
        Section section = firstSection.get();
        Lecture lecture = firstLecture.get();
        
        return RecentLearningResponse.builder()
                // Enrollment info
                .enrollmentId(enrollment.getId())
                .enrollmentStatus(enrollment.getStatus().toString())
                .enrollmentProgressPercentage(enrollment.getProgressPercentage())
                .lastAccessedAt(enrollment.getLastAccessedAt() != null ? 
                    enrollment.getLastAccessedAt().atOffset(java.time.ZoneOffset.UTC) : null)
                
                // Course info
                .courseId(course.getCourseId())
                .courseTitle(course.getTitle())
                .courseDescription(course.getDescription())
                .courseImage(course.getImage())
                
                // Section info
                .sectionId(section.getSectionId())
                .sectionTitle(section.getTitle())
                .position(section.getPosition())
                
                // First lecture info
                .lectureId(lecture.getLectureId())
                .lectureTitle(lecture.getTitle())
                .lectureVideoUrl(lecture.getSourceUrl())
                .lectureDuration(lecture.getDuration())
                .lectureOrder(lecture.getPosition())
                
                // Progress info (empty for first lecture)
                .progressId(null)
                .isLectureCompleted(false)
                .lastViewedAt(null)
                .progressUpdatedAt(null)
                .lectureProgressPercentage(0.0)
                
                // Learning session info
                .currentLearningStatus("START_NEW")
                .recommendedAction("Start learning this course")
                .build();
    }
    
}
