package com.pbl.elearning.course.service;

import com.pbl.elearning.course.payload.request.ResourceRequest;
import com.pbl.elearning.course.payload.response.ResourceResponse;

import java.util.UUID;

public interface ResourceService {
    ResourceResponse createResource(ResourceRequest resourceRequest, UUID LectureId);
    ResourceResponse getResourceById(UUID resourceId);
    ResourceResponse updateResource(UUID resourceId, ResourceRequest resourceRequest);
    void deleteResource(UUID resourceId);
}
