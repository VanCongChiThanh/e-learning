package com.pbl.elearning.web.endpoint.course;

import com.pbl.elearning.common.constant.CommonConstant;
import com.pbl.elearning.common.payload.general.ResponseDataAPI;
import com.pbl.elearning.course.payload.request.LectureRequest;
import com.pbl.elearning.course.payload.response.LectureResponse;
import com.pbl.elearning.course.service.LectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/sections/{sectionId}/lectures")
public class LectureController {
    private final LectureService lectureService;

    @PostMapping
    public ResponseEntity<ResponseDataAPI> createLecture(
            @PathVariable UUID sectionId,
            @Valid @RequestBody LectureRequest request) {
        LectureResponse lectureResponse = lectureService.createLecture(request, sectionId);
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data(lectureResponse)
                .build());
    }

    @GetMapping("/{lectureId}")
    public ResponseEntity<ResponseDataAPI> getLectureById(@PathVariable UUID lectureId) {
        LectureResponse lectureResponse = lectureService.getLectureById(lectureId);
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data(lectureResponse)
                .build());
    }

    @PutMapping("/{lectureId}")
    public ResponseEntity<ResponseDataAPI> updateLecture(
            @PathVariable UUID lectureId,
            @Valid @RequestBody LectureRequest request) {
        LectureResponse updatedLecture = lectureService.updateLecture(lectureId, request);
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data(updatedLecture)
                .build());
    }
    @DeleteMapping("/{lectureId}")
    public ResponseEntity<ResponseDataAPI> deleteLecture(@PathVariable UUID lectureId) {
        lectureService.deleteLecture(lectureId);
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data("Lecture deleted successfully")
                .build());
    }
}
