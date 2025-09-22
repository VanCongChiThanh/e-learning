package com.pbl.elearning.web.endpoint.course;

import com.pbl.elearning.common.constant.CommonConstant;
import com.pbl.elearning.common.payload.general.ResponseDataAPI;
import com.pbl.elearning.course.payload.request.ResourceRequest;
import com.pbl.elearning.course.payload.response.ResourceResponse;
import com.pbl.elearning.course.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/lectures/{lectureId}/resources")
public class ResourceController {
    private final ResourceService resourceService;

    @PostMapping
    public ResponseEntity<ResponseDataAPI> createResource(
            @PathVariable UUID lectureId,
            @Valid @RequestBody ResourceRequest request) {
        ResourceResponse resourceResponse = resourceService.createResource(request, lectureId);
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data(resourceResponse)
                .build());
    }

    @GetMapping("/{resourceId}")
    public ResponseEntity<ResponseDataAPI> getResourceById(@PathVariable UUID resourceId) {
        ResourceResponse resourceResponse = resourceService.getResourceById(resourceId);
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data(resourceResponse)
                .build());
    }

    @PutMapping("/{resourceId}")
    public ResponseEntity<ResponseDataAPI> updateResource(
            @PathVariable UUID resourceId,
            @Valid @RequestBody ResourceRequest request) {
        ResourceResponse updatedResource = resourceService.updateResource(resourceId, request);
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data(updatedResource)
                .build());
    }

    @DeleteMapping("/{resourceId}")
    public ResponseEntity<ResponseDataAPI> deleteResource(@PathVariable UUID resourceId) {
        resourceService.deleteResource(resourceId);
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data("Resource deleted successfully")
                .build());
    }
}
