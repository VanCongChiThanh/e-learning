package com.pbl.elearning.course.service.impl;

import com.pbl.elearning.course.domain.Tag;
import com.pbl.elearning.course.payload.request.TagRequest;
import com.pbl.elearning.course.payload.response.TagResponse;
import com.pbl.elearning.course.repository.TagRepository;
import com.pbl.elearning.course.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;

    @Override
    public TagResponse createTag(TagRequest tagRequest){
        if(tagRepository.existsByName(tagRequest.getName())){
            throw new RuntimeException("Tag already exists with name: " + tagRequest.getName());
        }
        Tag tag = Tag.builder()
                .name(tagRequest.getName())
                .build();
        Tag savedTag = tagRepository.save(tag);
        return TagResponse.fromEntity(savedTag);

    }

    @Override
    public Set<TagResponse> getAllTags(){
        return tagRepository.findAll().stream()
                .map(TagResponse::fromEntity)
                .collect(java.util.stream.Collectors.toSet());
    }

    @Override
    public Set<TagResponse> getTagsByCourseId(UUID courseId){
        return tagRepository.findByCourses_CourseId(courseId).stream()
                .map(TagResponse::fromEntity)
                .collect(java.util.stream.Collectors.toSet());
    }

    @Override
    public TagResponse getTagById(UUID tagId){
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() ->
                        new RuntimeException("Tag not found with id: " + tagId));
        return TagResponse.fromEntity(tag);
    }

    @Override
    public TagResponse updateTag(UUID tagId, TagRequest tagRequest){
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() ->
                        new RuntimeException("Tag not found with id: " + tagId));
        if(tagRepository.existsByName(tagRequest.getName())){
            throw new RuntimeException("Tag already exists with name: " + tagRequest.getName());
        }
        tag.setName(tagRequest.getName());
        Tag updatedTag = tagRepository.save(tag);
        return TagResponse.fromEntity(updatedTag);
    }
    @Override
    public void deleteTag(UUID tagId){
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() ->
                        new RuntimeException("Tag not found with id: " + tagId));
        tagRepository.delete(tag);
    }


}
