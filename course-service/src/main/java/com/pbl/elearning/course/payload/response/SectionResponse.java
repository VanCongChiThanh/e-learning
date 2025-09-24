package com.pbl.elearning.course.payload.response;

import com.pbl.elearning.course.domain.Section;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
public class SectionResponse {
    private UUID sectionId;
    private UUID courseId;
    private String title;
    private Integer position;
    private List<LectureResponse> lectures;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public static SectionResponse fromEntity(Section section) {
        return SectionResponse.builder()
                .sectionId(section.getSectionId())
                .courseId(section.getCourse() != null ? section.getCourse().getCourseId() : null)
                .title(section.getTitle())
                .position(section.getPosition())
                .lectures(section.getLectures() != null ?
                        section.getLectures().stream()
                                .map(LectureResponse::fromEntity)
                                .collect(Collectors.toList()) :
                        null)
                .createdAt(section.getCreatedAt())
                .updatedAt(section.getUpdatedAt())
                .build();
    }

    public static SectionResponse fromEntityWithoutLectures(Section section) {
        return SectionResponse.builder()
                .sectionId(section.getSectionId())
                .courseId(section.getCourse() != null ? section.getCourse().getCourseId() : null)
                .title(section.getTitle())
                .position(section.getPosition())
                .createdAt(section.getCreatedAt())
                .updatedAt(section.getUpdatedAt())
                .build();
    }

}
