package com.pbl.elearning.course.service.impl;

import com.pbl.elearning.course.domain.Course;
import com.pbl.elearning.course.domain.Section;
import com.pbl.elearning.course.payload.request.SectionRequest;
import com.pbl.elearning.course.payload.response.SectionResponse;
import com.pbl.elearning.course.repository.CourseRepository;
import com.pbl.elearning.course.repository.SectionRepository;
import com.pbl.elearning.course.service.SectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SectionServiceImpl implements SectionService {

    private  final SectionRepository sectionRepository;
    private  final CourseRepository courseRepository;

    @Override
    public SectionResponse createSection(SectionRequest sectionRequest, UUID courseId){
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Course not found with id: " + courseId));
        Section section= Section.builder()
                .title(sectionRequest.getTitle())
                .position(sectionRequest.getPosition())
                .course(course)
                .build();

        return  SectionResponse.fromEntityWithoutLectures(section);

    }
    @Override
    public SectionResponse getSectionById(UUID sectionId){
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Section not found with id: " + sectionId));
        return SectionResponse.fromEntityWithoutLectures(section);
    }

    @Override
    public List<SectionResponse> getAllSections(UUID courseId){
        List<Section> sections = sectionRepository.findByCourse_CourseId(courseId);
        return sections.stream()
                .map(SectionResponse::fromEntityWithoutLectures)
                .toList();
    }
    @Override
    public SectionResponse updateSection(UUID sectionId, SectionRequest sectionRequest) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Section not found with id: " + sectionId));
        section.setTitle(sectionRequest.getTitle());
        section.setPosition(sectionRequest.getPosition());
        Section updatedSection = sectionRepository.save(section);
        return SectionResponse.fromEntityWithoutLectures(updatedSection);
    }
    @Override
    public void deleteSection(UUID sectionId) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Section not found with id: " + sectionId));
        sectionRepository.delete(section);
    }



}