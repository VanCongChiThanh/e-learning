package com.pbl.elearning.web.endpoint.course;

import com.pbl.elearning.common.constant.CommonConstant;
import com.pbl.elearning.common.payload.general.ResponseDataAPI;
import com.pbl.elearning.course.payload.request.SectionRequest;
import com.pbl.elearning.course.payload.response.SectionResponse;
import com.pbl.elearning.course.service.impl.SectionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/courses/{courseId}/sections")
public class SectionController {

    private  final  SectionServiceImpl sectionService;

    @PostMapping
    public ResponseEntity<ResponseDataAPI> createSection(
            @PathVariable UUID courseId,
            @Valid @RequestBody SectionRequest request) {
        SectionResponse sectionResponse = sectionService.createSection(request, courseId);
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data(sectionResponse)
                .build());
    }

    @GetMapping("/{sectionId}")
    public ResponseEntity<ResponseDataAPI> getSectionById(@PathVariable UUID sectionId) {
        SectionResponse sectionResponse = sectionService.getSectionById(sectionId);
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data(sectionResponse)
                .build());
    }

    @PutMapping("/{sectionId}")
    public ResponseEntity<ResponseDataAPI> updateSection(
            @PathVariable UUID sectionId,
            @Valid @RequestBody SectionRequest request) {
        SectionResponse updatedSection = sectionService.updateSection(sectionId, request);
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data(updatedSection)
                .build());
    }

    @DeleteMapping("/{sectionId}")
    public ResponseEntity<ResponseDataAPI> deleteSection(@PathVariable UUID sectionId) {
        sectionService.deleteSection(sectionId);
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data("Section deleted successfully")
                .build());
    }

}
