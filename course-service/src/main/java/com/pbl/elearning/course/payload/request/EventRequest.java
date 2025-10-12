package com.pbl.elearning.course.payload.request;

import com.pbl.elearning.course.domain.enums.EventType;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class EventRequest {
    @NotNull(message = "Lecture ID không được để trống")
    private UUID lectureId;

    @NotNull(message = "Loại sự kiện không được để trống")
    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @NotNull(message = "Thời điểm kích hoạt không được để trống")
    @Min(value = 0, message = "Thời điểm kích hoạt phải là một số không âm")
    private Integer triggerTime;

    @NotBlank(message = "Payload không được để trống")
    private String payload;
}
