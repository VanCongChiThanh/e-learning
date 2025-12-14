package com.pbl.elearning.course.repository;

import com.pbl.elearning.course.domain.Course;
import com.pbl.elearning.course.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID>, JpaSpecificationExecutor<Review> {
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.course.courseId = :courseId")
    Double findAverageRatingByCourseId(UUID courseId);
    Integer countByCourse_CourseId(UUID courseId);
    boolean existsByCourse_CourseIdAndUserId(UUID courseId, UUID userId);
    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.course.courseId = :courseId GROUP BY r.rating")
    List<Object[]> countReviewsByRatingGroup(UUID courseId);
}
