package com.pbl.elearning.course.service;

import com.pbl.elearning.course.payload.request.NoteRequest;
import com.pbl.elearning.course.payload.response.NoteResponse;

import java.util.List;
import java.util.UUID;

public interface NoteService {
    NoteResponse createNote(NoteRequest noteRequest, UUID lectureId, UUID userId);
    List<NoteResponse> getAllNotesByLectureId(UUID lectureId, UUID userId);
    NoteResponse getNoteById(UUID noteId);
    NoteResponse updateNote(UUID noteId, NoteRequest noteRequest);
    void deleteNote(UUID noteId);
}
