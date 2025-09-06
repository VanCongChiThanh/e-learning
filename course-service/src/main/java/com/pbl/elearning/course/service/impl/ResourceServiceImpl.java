package com.pbl.elearning.course.service.impl;

import com.pbl.elearning.course.domain.Lecture;
import com.pbl.elearning.course.domain.Resource;
import com.pbl.elearning.course.payload.request.ResourceRequest;
import com.pbl.elearning.course.payload.response.ResourceResponse;
import com.pbl.elearning.course.repository.LectureRepository;
import com.pbl.elearning.course.repository.ResourceRepository;
import com.pbl.elearning.course.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ResourceServiceImpl implements ResourceService {
    private final ResourceRepository resourceRepository;
    private final LectureRepository lectureRepository;

    @Override
    public ResourceResponse createResource(ResourceRequest resourceRequest, UUID lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() ->
                        new RuntimeException("Lecture not found with id: " + lectureId));
        Resource resource = Resource.builder()
                .fileUrl(resourceRequest.getFileUrl())
                .fileType(resourceRequest.getFileType())
                .lecture(lecture)
                .build();
        Resource savedResource = resourceRepository.save(resource);
        return ResourceResponse.fromEntity(savedResource);
    }

    @Override
    public ResourceResponse getResourceById(UUID resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() ->
                        new RuntimeException("Resource not found with id: " + resourceId));
        return ResourceResponse.fromEntity(resource);
    }

    @Override
    public ResourceResponse updateResource(UUID resourceId, ResourceRequest resourceRequest) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() ->
                        new RuntimeException("Resource not found with id: " + resourceId));
        resource.setFileUrl(resourceRequest.getFileUrl());
        resource.setFileType(resourceRequest.getFileType());
        Resource updatedResource = resourceRepository.save(resource);
        return ResourceResponse.fromEntity(updatedResource);
    }

    @Override
    public void deleteResource(UUID resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() ->
                        new RuntimeException("Resource not found with id: " + resourceId));
        resourceRepository.delete(resource);
    }


}
