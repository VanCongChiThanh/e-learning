package com.pbl.elearning.course.repository;

import com.pbl.elearning.course.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {
    boolean existsByName(String name);
    Set<Tag> findByCourses_CourseId(UUID courseId);
}
