package com.pbl.elearning.course.service;

import com.pbl.elearning.course.payload.request.SectionRequest;
import com.pbl.elearning.course.payload.response.SectionResponse;

import java.util.UUID;

public interface SectionService {
    SectionResponse createSection(SectionRequest sectionRequest, UUID courseId);
    SectionResponse getSectionById(UUID sectionId);
    SectionResponse updateSection(UUID sectionId, SectionRequest sectionRequest);
    void deleteSection(UUID sectionId);
}
