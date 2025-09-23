package com.pbl.elearning.course.payload.response;

import com.pbl.elearning.course.domain.Lecture;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
public class LectureResponse {
    private UUID lectureId;
    private UUID sectionId;
    private String title;
    private String content;
    private Integer position;
    private String videoUrl;
    private Integer duration;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public static LectureResponse fromEntity(Lecture lecture) {
        return LectureResponse.builder()
                .lectureId(lecture.getLectureId())
                .sectionId(lecture.getSection() != null ? lecture.getSection().getSectionId() : null)
                .title(lecture.getTitle())
                .position(lecture.getPosition())
                .videoUrl(lecture.getSourceUrl())
                .duration(lecture.getDuration())
                .createdAt(lecture.getCreatedAt())
                .updatedAt(lecture.getUpdatedAt()).build();

    }
}
