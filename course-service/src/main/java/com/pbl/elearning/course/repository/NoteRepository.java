package com.pbl.elearning.course.repository;

import com.pbl.elearning.course.domain.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NoteRepository extends JpaRepository<Note, UUID> {
    List<Note> findByLecture_LectureIdAndUserId(UUID lectureId, UUID userId);
}
