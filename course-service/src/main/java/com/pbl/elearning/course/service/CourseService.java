package com.pbl.elearning.course.service;

import com.pbl.elearning.course.domain.Tag;
import com.pbl.elearning.course.payload.request.CourseRequest;
import com.pbl.elearning.course.payload.response.CoursePageResponse;
import com.pbl.elearning.course.payload.response.CourseResponse;
import com.pbl.elearning.course.payload.response.TagResponse;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.stylesheets.LinkStyle;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface CourseService {
    CourseResponse createCourse(CourseRequest courseRequest, UUID instructorId);
    List<CourseResponse> getAllCourses(int page, int size);
    CoursePageResponse coursePageResponse(int page, int size);
    CourseResponse getCourseById(UUID courseId);
    String uploadCourseImage(UUID courseId, String urlfile);
    CourseResponse updateCourse(UUID courseId, CourseRequest courseRequest);
    void deleteCourse(UUID courseId);

    // course_tag

    CourseResponse addTagsToCourse(UUID courseId, Set<UUID> tagIds);
    CourseResponse removeTagFromCourse(UUID courseId, UUID tagIds);
    Set<TagResponse> getTagsByCourseId(UUID courseId);







//    CourseResponse updateCourse(UUID courseId, CourseRequest courseRequest, UUID instructorId);

//    CourseResponse updateCourseStatus(UUID courseId, CourseStatus status, UUID instructorId);
}
