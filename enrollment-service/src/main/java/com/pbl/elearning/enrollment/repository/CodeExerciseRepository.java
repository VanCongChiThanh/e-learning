package com.pbl.elearning.enrollment.repository;

import com.pbl.elearning.enrollment.models.CodeExercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CodeExerciseRepository extends JpaRepository<CodeExercise, UUID> {
    List<CodeExercise> findAllByLecture_LectureId(UUID lectureId);
}
