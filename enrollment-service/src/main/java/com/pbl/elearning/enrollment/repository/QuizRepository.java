package com.pbl.elearning.enrollment.repository;

import com.pbl.elearning.enrollment.models.Quiz;
import com.pbl.elearning.course.domain.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuizRepository extends JpaRepository<Quiz, UUID> {
    List<Quiz> findByLecture(Lecture lecture);
    List<Quiz> findByLecture_LectureId(UUID lectureId);
}
