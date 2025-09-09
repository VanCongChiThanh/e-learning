package com.pbl.elearning.web.endpoint.course;

import com.pbl.elearning.common.constant.CommonConstant;
import com.pbl.elearning.common.payload.general.ResponseDataAPI;
import com.pbl.elearning.course.payload.request.NoteRequest;
import com.pbl.elearning.course.payload.response.NoteResponse;
import com.pbl.elearning.course.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/lectures/{lectureId}/notes")
public class NoteController {
    private final NoteService noteService;

    @PostMapping
    public ResponseEntity<ResponseDataAPI> createNote(
            @PathVariable UUID lectureId,
            @RequestHeader("X-User-ID") UUID userId,
            @Valid @RequestBody NoteRequest request) {
        NoteResponse noteResponse = noteService.createNote(request, lectureId, userId);
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data(noteResponse)
                .build());
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<ResponseDataAPI> getNoteById(@PathVariable UUID noteId) {
        NoteResponse noteResponse = noteService.getNoteById(noteId);
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data(noteResponse)
                .build());
    }
    @PutMapping("/{noteId}")
    public ResponseEntity<ResponseDataAPI> updateNote(
            @PathVariable UUID noteId,
            @Valid @RequestBody NoteRequest request) {
        NoteResponse updatedNote = noteService.updateNote(noteId, request);
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data(updatedNote)
                .build());
    }
    @DeleteMapping("/{noteId}")
    public ResponseEntity<ResponseDataAPI> deleteNote(@PathVariable UUID noteId) {
        noteService.deleteNote(noteId);
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data("Note deleted successfully")
                .build());
    }
}
