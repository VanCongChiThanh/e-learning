package com.pbl.elearning.course.repository;

import com.pbl.elearning.course.domain.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface LectureRepository extends JpaRepository<Lecture, UUID> {
    List<Lecture> findBySection_SectionId(UUID sectionId);

    Integer countBySection_Course_CourseId(UUID courseId);
    List<Lecture> findBySection_Course_CourseId(UUID courseId);
}
