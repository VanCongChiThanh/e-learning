package com.pbl.elearning.course.service;

import com.pbl.elearning.course.domain.Event;
import com.pbl.elearning.course.payload.response.EventResponse;

import java.util.List;
import java.util.UUID;

public interface EventService {
    List<EventResponse>  getEventsByLectureId(UUID lectureId);
}
