package com.pbl.elearning.course.service.impl;

import com.pbl.elearning.course.domain.Event;
import com.pbl.elearning.course.payload.response.EventResponse;
import com.pbl.elearning.course.repository.EventRepository;
import com.pbl.elearning.course.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Override
    public List<EventResponse> getEventsByLectureId(UUID lectureId) {
        List<Event> events = eventRepository.findByLecture_LectureId(lectureId);
        return events.stream()
                .map(EventResponse::toEventResponse)
                .toList();


    }
}
