package com.pbl.elearning.course.service;

import com.pbl.elearning.course.payload.request.TagRequest;
import com.pbl.elearning.course.payload.response.TagResponse;

import java.util.Set;
import java.util.UUID;

public interface TagService {
    TagResponse createTag(TagRequest tagRequest);
    Set<TagResponse> getAllTags();
    Set<TagResponse> getTagsByCourseId(UUID courseId);
    TagResponse getTagById(UUID tagId);
    TagResponse updateTag(UUID tagId, TagRequest tagRequest);
    void deleteTag(UUID tagId);

}
