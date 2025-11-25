package com.pbl.elearning.enrollment.repository;

import com.pbl.elearning.enrollment.models.Enrollment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {
    
    List<Enrollment> findByUser_UserId(UUID userId);
    
    @Query("SELECT e FROM Enrollment e WHERE e.course.courseId = :courseId")
    List<Enrollment> findByCourseId(@Param("courseId") UUID courseId);
    
    @Query("SELECT e FROM Enrollment e WHERE e.user.id = :userId AND e.course.courseId = :courseId")
    Optional<Enrollment> findByUserIdAndCourseId(@Param("userId") UUID userId, @Param("courseId") UUID courseId);
    
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.courseId = :courseId")
    Long countByCourseId(@Param("courseId") UUID courseId);
    
    @Query("SELECT e FROM Enrollment e WHERE e.user.id = :userId AND e.status = :status")
    List<Enrollment> findByUserIdAndStatus(@Param("userId") UUID userId, @Param("status") String status);

    boolean existsByUserIdAndCourse_CourseId(UUID userId, UUID courseId);
}
