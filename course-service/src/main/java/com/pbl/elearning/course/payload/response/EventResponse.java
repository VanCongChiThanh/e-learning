package com.pbl.elearning.course.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pbl.elearning.course.domain.Event;
import com.pbl.elearning.course.domain.enums.EventType;
import lombok.Builder;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventResponse {
    private UUID id;
    private UUID lectureId;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    private Integer triggerTime;
    private String payload;

    public static EventResponse toEventResponse(Event event) {
        if (event == null) {
            return null;
        }

        return EventResponse.builder()
                .id(event.getId())
                .lectureId(event.getLecture().getLectureId())
                .eventType(event.getEventType())
                .triggerTime(event.getTriggerTime())
                .payload(event.getPayload())
                .build();
    }
}
