package com.pbl.elearning.course.repository;

import com.pbl.elearning.course.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.course.courseId = :courseId")
    Double findAverageRatingByCourseId(UUID courseId);
    Integer countByCourse_CourseId(UUID courseId);
}
