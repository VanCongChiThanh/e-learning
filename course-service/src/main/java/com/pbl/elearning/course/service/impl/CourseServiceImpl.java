package com.pbl.elearning.course.service.impl;

import com.pbl.elearning.common.payload.general.PageInfo;
import com.pbl.elearning.course.domain.Course;
import com.pbl.elearning.course.domain.Tag;
import com.pbl.elearning.course.domain.enums.CourseLevel;
import com.pbl.elearning.course.payload.request.CourseRequest;
import com.pbl.elearning.course.payload.response.CoursePageResponse;
import com.pbl.elearning.course.payload.response.CourseResponse;
import com.pbl.elearning.course.payload.response.TagResponse;
import com.pbl.elearning.course.repository.CourseRepository;
import com.pbl.elearning.course.repository.TagRepository;
import com.pbl.elearning.course.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.hibernate.proxy.EntityNotFoundDelegate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final TagRepository tagRepository;



    @Override
    public CourseResponse createCourse(CourseRequest courseRequest, UUID instructorId){

        Course course = Course.builder()
                .title(courseRequest.getTitle())
                .description(courseRequest.getDescription())
                .price(courseRequest.getPrice())
                .level(courseRequest.getLevel())
                .category(courseRequest.getCategory())
                .instructorId(instructorId)

                .build();

        Course savedCourse = courseRepository.save(course);
        return CourseResponse.builder()
                .title(savedCourse.getTitle())
                .description(savedCourse.getDescription())
                .price(savedCourse.getPrice())
                .level(savedCourse.getLevel())
                .category(savedCourse.getCategory())
                .instructorId(savedCourse.getInstructorId())

                .build();
    }

    @Override
    public List<CourseResponse> getAllCourses(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Course> coursePage = courseRepository.findAll(pageable);

        return coursePage.getContent().stream()
                .map(course -> CourseResponse.builder()
                        .courseId(course.getCourseId())
                        .title(course.getTitle())
                        .description(course.getDescription())
                        .price(course.getPrice())
                        .level(course.getLevel())
                        .category(course.getCategory())
                        .instructorId(course.getInstructorId())
                        .build())
                .toList();
    }
    @Override
    public Page<CourseResponse> coursePageResponse(Pageable pageable){
        Page<Course> coursePage = courseRepository.findAll(pageable);
        return coursePage.map(CourseResponse::toCourseResponse);
    }


    @Override
     public CourseResponse getCourseById(UUID courseId){
        Course course= courseRepository.findById(courseId).orElseThrow(() ->
            new EntityNotFoundException("Course not found with id: " + courseId));
        return CourseResponse.toCourseResponse(course);
    }
    @Override
    public  String uploadCourseImage(UUID courseId, String urlfile){
        Course course= courseRepository.findById(courseId).orElseThrow(() ->
                new EntityNotFoundException("Course not found with id: " + courseId));
        course.setImage(urlfile);
        courseRepository.save(course);
        return urlfile;
    }
    @Override
    public CourseResponse updateCourse(UUID courseId, CourseRequest courseRequest){
        Course course= courseRepository.findById(courseId).orElseThrow(() ->
                new EntityNotFoundException("Course not found with id: " + courseId));
        course.setTitle(courseRequest.getTitle());
        course.setDescription(courseRequest.getDescription());
        course.setPrice(courseRequest.getPrice());
        course.setLevel(courseRequest.getLevel());
        course.setCategory(courseRequest.getCategory());
        Course updatedCourse = courseRepository.save(course);
        return CourseResponse.toCourseResponse(updatedCourse);
    }

    @Override
    public void deleteCourse(UUID courseId){
        Course course= courseRepository.findById(courseId).orElseThrow(() ->
                new EntityNotFoundException("Course not found with id: " + courseId));
        courseRepository.delete(course);

    }

    // implement course_tag methods

    @Override
    public CourseResponse addTagsToCourse(UUID courseId, Set<UUID> tagIds){
        Course course= courseRepository.findById(courseId).orElseThrow(() ->
                new EntityNotFoundException("Course not found with id: " + courseId));
        Set<Tag> tagsToAdd = new HashSet<>(tagRepository.findAllById(tagIds));
        course.getTags().addAll(tagsToAdd);
        Course updatedCourse = courseRepository.save(course);
        return CourseResponse.toCourseResponse(updatedCourse);
    }

    @Override
    public CourseResponse removeTagFromCourse(UUID courseId, UUID tagId){
        Course course= courseRepository.findById(courseId).orElseThrow(() ->
                new EntityNotFoundException("Course not found with id: " + courseId));
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found with id: " + tagId));
        course.getTags().remove(tag);
        Course updatedCourse = courseRepository.save(course);
        return CourseResponse.toCourseResponse(updatedCourse);
    }

    @Override
    public Set<TagResponse> getTagsByCourseId(UUID courseId){
        Course course= courseRepository.findById(courseId).orElseThrow(() ->
                new EntityNotFoundException("Course not found with id: " + courseId));
        Set<Tag> tags = course.getTags();
        return tags.stream()
                .map(TagResponse::fromEntity)
                .collect(java.util.stream.Collectors.toSet());
    }





}
