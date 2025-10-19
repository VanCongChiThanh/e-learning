package com.pbl.elearning.course.payload.response;

import com.pbl.elearning.course.domain.Note;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
public class NoteResponse {
    private UUID noteId;
    private UUID lectureId;
    private String lectureTitle;
    private UUID userId;
    private String content;
    private Timestamp createdAt;
    private Timestamp deletedAt;
    private Long videoTimestamp;

    public static NoteResponse fromEntity(Note note) {
        return NoteResponse.builder()
                .noteId(note.getNoteId())
                .lectureId(note.getLecture() != null ? note.getLecture().getLectureId() : null)
                .lectureTitle(note.getLecture() != null ? note.getLecture().getTitle() : null)
                .userId(note.getUserId())
                .content(note.getContent())
                .createdAt(note.getCreatedAt())
                .deletedAt(note.getDeletedAt())
                .videoTimestamp(note.getVideoTimestamp())
                .build();
    }

}
