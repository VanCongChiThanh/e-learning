package com.pbl.elearning.course.service;

import com.pbl.elearning.course.domain.Course;
import com.pbl.elearning.course.domain.Tag;
import com.pbl.elearning.course.domain.enums.Category;
import com.pbl.elearning.course.payload.request.CourseRequest;
import com.pbl.elearning.course.payload.response.CoursePageResponse;
import com.pbl.elearning.course.payload.response.CourseResponse;
import com.pbl.elearning.course.payload.response.TagResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.stylesheets.LinkStyle;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface CourseService {
    CourseResponse createCourse(CourseRequest courseRequest, UUID instructorId);
    List<CourseResponse> getAllCourses(int page, int size);
    Page<CourseResponse> coursePageResponse(Pageable pageable, Specification<Course> spec);
    CourseResponse getCourseById(UUID courseId);
    String uploadCourseImage(UUID courseId, String urlfile);
    CourseResponse updateCourse(UUID courseId, CourseRequest courseRequest);
    void deleteCourse(UUID courseId);

    // course_tag

    CourseResponse addTagsToCourse(UUID courseId, Set<UUID> tagIds);
    CourseResponse removeTagFromCourse(UUID courseId, UUID tagIds);
    Set<TagResponse> getTagsByCourseId(UUID courseId);

    // use slug
    CourseResponse getCourseBySlug(String slug);
    CourseResponse getCourseDetailBySlug(String slug);
    String uploadCourseImageBySlug(String slug, String urlfile);
    CourseResponse updateCourseBySlug(String slug, CourseRequest courseRequest);
    void deleteCourseBySlug(String slug);





//    CourseResponse updateCourse(UUID courseId, CourseRequest courseRequest, UUID instructorId);

//    CourseResponse updateCourseStatus(UUID courseId, CourseStatus status, UUID instructorId);
}