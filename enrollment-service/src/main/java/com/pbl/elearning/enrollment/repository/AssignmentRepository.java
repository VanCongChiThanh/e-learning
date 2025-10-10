package com.pbl.elearning.enrollment.repository;

import com.pbl.elearning.enrollment.models.Assignment;
import com.pbl.elearning.course.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, UUID> {
    List<Assignment> findByCourse(Course course);
    List<Assignment> findByCourse_CourseId(UUID courseId);
}