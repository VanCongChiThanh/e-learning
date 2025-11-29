package com.pbl.elearning.enrollment.repository;

import com.pbl.elearning.enrollment.models.Progress;
import com.pbl.elearning.enrollment.models.Enrollment;
import com.pbl.elearning.course.domain.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProgressRepository extends JpaRepository<Progress, UUID> {
    List<Progress> findByEnrollment(Enrollment enrollment);
    List<Progress> findByEnrollment_Id(UUID enrollmentId);
    List<Progress> findByLecture(Lecture lecture);
    List<Progress> findByLecture_LectureId(UUID lectureId);
    Optional<Progress> findByEnrollmentAndLecture(Enrollment enrollment, Lecture lecture);
    
    // New methods for progress tracking
    Optional<Progress> findByEnrollment_User_UserIdAndLecture_LectureId(UUID userId, UUID lectureId);
    List<Progress> findByEnrollment_Course_CourseIdAndIsCompleted(UUID courseId, Boolean isCompleted);
    Long countByEnrollment_Course_CourseIdAndIsCompleted(UUID courseId, Boolean isCompleted);
}
