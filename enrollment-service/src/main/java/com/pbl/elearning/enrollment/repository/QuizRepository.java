package com.pbl.elearning.enrollment.repository;

import com.pbl.elearning.enrollment.models.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QuizRepository extends JpaRepository<Quiz, UUID> {
    List<Quiz> findBylectureId(UUID lectureId);
}
