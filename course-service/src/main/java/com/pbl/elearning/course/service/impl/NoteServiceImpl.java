package com.pbl.elearning.course.service.impl;

import com.pbl.elearning.course.domain.Lecture;
import com.pbl.elearning.course.domain.Note;
import com.pbl.elearning.course.payload.request.NoteRequest;
import com.pbl.elearning.course.payload.response.NoteResponse;
import com.pbl.elearning.course.repository.LectureRepository;
import com.pbl.elearning.course.repository.NoteRepository;
import com.pbl.elearning.course.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class NoteServiceImpl implements NoteService {
    private final NoteRepository noteRepository;
    private final LectureRepository lectureRepository;

    @Override
    public NoteResponse createNote(NoteRequest noteRequest, UUID lectureId, UUID userId){
        Lecture lecture= lectureRepository.findById(lectureId)
                .orElseThrow(() ->
                        new RuntimeException("Lecture not found with id: " + lectureId));

        Note note = Note.builder()
                .content(noteRequest.getContent())
                .lecture(lecture)
                .userId(userId)
                .build();
        Note savedNote = noteRepository.save(note);
        return NoteResponse.fromEntity(savedNote);
    }
    @Override
    public List<NoteResponse> getAllNotesByLectureId(UUID lectureId){
        Lecture lecture= lectureRepository.findById(lectureId)
                .orElseThrow(() ->
                        new RuntimeException("Lecture not found with id: " + lectureId));
        List<Note> note = noteRepository.findByLecture_LectureId(lectureId);
        return note.stream()
                .map(NoteResponse::fromEntity)
                .toList();
    }

    @Override
    public NoteResponse getNoteById(UUID noteId){
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() ->
                        new RuntimeException("Note not found with id: " + noteId));
        return NoteResponse.fromEntity(note);
    }
    @Override
    public NoteResponse updateNote(UUID noteId, NoteRequest noteRequest) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() ->
                        new RuntimeException("Note not found with id: " + noteId));
        note.setContent(noteRequest.getContent());
        Note updatedNote = noteRepository.save(note);
        return NoteResponse.fromEntity(updatedNote);
    }
    @Override
    public void deleteNote(UUID noteId) {
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() ->
                        new RuntimeException("Note not found with id: " + noteId));
        noteRepository.delete(note);
    }

}
