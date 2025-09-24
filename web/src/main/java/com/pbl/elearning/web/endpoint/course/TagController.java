package com.pbl.elearning.web.endpoint.course;

import com.pbl.elearning.common.constant.CommonConstant;
import com.pbl.elearning.common.payload.general.ResponseDataAPI;
import com.pbl.elearning.course.payload.request.TagRequest;
import com.pbl.elearning.course.payload.response.TagResponse;
import com.pbl.elearning.course.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/tags")
public class TagController {
    private final TagService tagService;

    @PostMapping
    public ResponseEntity<ResponseDataAPI> createTag(@Valid @RequestBody TagRequest request) {
        TagResponse tagResponse = tagService.createTag(request);
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data(tagResponse)
                .build());
    }

    @GetMapping
    public ResponseEntity<ResponseDataAPI> getAllTags() {
        Set<TagResponse> tags = tagService.getAllTags();
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data(tags)
                .build());
    }

    @GetMapping("/{tagId}")
    public ResponseEntity<ResponseDataAPI> getTagById(@PathVariable UUID tagId) {
        TagResponse tagResponse = tagService.getTagById(tagId);
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data(tagResponse)
                .build());
    }

    @PutMapping("/{tagId}")
    public ResponseEntity<ResponseDataAPI> updateTag(
            @PathVariable UUID tagId,
            @Valid @RequestBody TagRequest request) {
        TagResponse updatedTag = tagService.updateTag(tagId, request);
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data(updatedTag)
                .build());
    }

    @DeleteMapping("/{tagId}")
    public ResponseEntity<ResponseDataAPI> deleteTag(@PathVariable UUID tagId) {
        tagService.deleteTag(tagId);
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data("Tag deleted successfully")
                .build());
    }

}
