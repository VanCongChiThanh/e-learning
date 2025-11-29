package com.pbl.elearning.course.repository;

import com.pbl.elearning.course.domain.Course;
import com.pbl.elearning.course.domain.enums.Category;
import com.pbl.elearning.course.domain.enums.CourseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID>, JpaSpecificationExecutor<Course> {
    Optional<Course> findByCourseIdAndCourseStatus(UUID courseId, CourseStatus status);

    Optional<Course> findBySlug(String slug);

    Page<Course> findByCategory(Category category, Pageable pageable);

    List<Course> findByInstructorId(UUID instructorId);

    boolean existsByCourseIdAndInstructorId(UUID courseId, UUID instructorId);

    @Query("""
                SELECT c
                FROM Course c
                WHERE c.courseId NOT IN (
                    SELECT e.course.courseId
                    FROM Enrollment e
                    WHERE e.user.id = :userId
                )
            """)
    Page<Course> findCoursesUserHasNotPurchased(UUID userId, Pageable pageable);
}