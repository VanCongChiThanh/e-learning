package com.pbl.elearning.web.endpoint.course;

import com.pbl.elearning.common.constant.CommonConstant;
import com.pbl.elearning.common.payload.general.ResponseDataAPI;
import com.pbl.elearning.course.payload.request.CourseRequest;
import com.pbl.elearning.course.payload.response.CoursePageResponse;
import com.pbl.elearning.course.payload.response.CourseResponse;
import com.pbl.elearning.course.service.CourseService;
import com.pbl.elearning.course.service.impl.CourseServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.mapstruct.MappingTarget;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/courses")
public class CourseController {
    private final CourseService courseService;
    @PostMapping
    public ResponseEntity<ResponseDataAPI> createCourse(
            @Valid
            @RequestBody CourseRequest courseRequest,
            @RequestHeader("X-User-ID") UUID instructorId) {
        return ResponseEntity.ok(ResponseDataAPI.builder()
                        .status(CommonConstant.SUCCESS)
                        .data(courseService.createCourse(courseRequest,instructorId))
                .build());

    }
    @GetMapping
    public ResponseEntity<ResponseDataAPI> getAllCourses(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data(courseService.getAllCourses(page, size))
                .build());
    }

    @GetMapping("/page")
    public ResponseEntity<ResponseDataAPI> getCoursePage(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        CoursePageResponse coursePageResponse = courseService.coursePageResponse(page, size);
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data( coursePageResponse)
                .build());
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<ResponseDataAPI> getCourseById(
            @PathVariable("courseId") UUID courseId) {
        CourseResponse courseResponse = courseService.getCourseById(courseId);

        return ResponseEntity.ok(ResponseDataAPI.builder()
                        .status(CommonConstant.SUCCESS)
                        .data(courseResponse)
                .build());

    }
    @PostMapping("/{courseId}/image")
    public ResponseEntity<ResponseDataAPI> uploadCourseImage(
            @PathVariable("courseId") UUID courseId,
            @RequestParam("imageUrl") String imageUrl) {

        // Chỉ lưu URL vào database
        String savedImageUrl = courseService.uploadCourseImage(courseId, imageUrl);

        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data(savedImageUrl)
                .build());
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<ResponseDataAPI> updateCourse(
            @PathVariable UUID courseId,
            @Valid @RequestBody CourseRequest courseRequest) {
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data(courseService.updateCourse(courseId, courseRequest))
                .build());
    }
    @DeleteMapping("/{courseId}")
    public ResponseEntity<ResponseDataAPI> deleteCourse(@PathVariable UUID courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data("Course deleted successfully")
                .build());
    }
    // --- ADD TAGS TO COURSE ---
    @PutMapping("/{courseId}/tags")
    public ResponseEntity<ResponseDataAPI> addTagsToCourse(
            @PathVariable UUID courseId,
            @RequestBody Set<UUID> tagIds) {
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data(courseService.addTagsToCourse(courseId, tagIds))
                .build());
    }
    @DeleteMapping("/{courseId}/tags/{tagId}")
    public ResponseEntity<ResponseDataAPI> removeTagFromCourse(
            @PathVariable UUID courseId,
            @PathVariable UUID tagId) {
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data(courseService.removeTagFromCourse(courseId, tagId))
                .build());
    }
    @GetMapping("/{courseId}/tags")
    public ResponseEntity<ResponseDataAPI> getTagsByCourseId(@PathVariable UUID courseId) {
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data(courseService.getTagsByCourseId(courseId))
                .build());
    }

}
