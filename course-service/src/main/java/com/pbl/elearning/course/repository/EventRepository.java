package com.pbl.elearning.course.repository;

import com.pbl.elearning.course.domain.Event;
import com.pbl.elearning.course.payload.response.EventResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {
    List<Event> findByLecture_LectureId(UUID lectureId);
}
