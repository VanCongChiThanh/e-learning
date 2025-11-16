package com.pbl.elearning.web.endpoint.course;

import com.pbl.elearning.common.util.PagingUtils;
import com.pbl.elearning.common.constant.CommonConstant;
import com.pbl.elearning.common.payload.general.PageInfo;
import com.pbl.elearning.common.payload.general.ResponseDataAPI;
import com.pbl.elearning.course.domain.Course;
import com.pbl.elearning.course.payload.request.CourseRequest;
import com.pbl.elearning.course.payload.response.CourseResponeInstructor;
import com.pbl.elearning.course.payload.response.CourseResponse;
import com.pbl.elearning.course.service.CourseService;
import com.pbl.elearning.security.annotation.CurrentUser;
import com.pbl.elearning.security.domain.UserPrincipal;
import com.turkraft.springfilter.boot.Filter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/courses")
@Slf4j
public class CourseController {
    private final CourseService courseService;
    @PostMapping
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<ResponseDataAPI> createCourse(
            @Valid
            @RequestBody CourseRequest courseRequest,
            @CurrentUser UserPrincipal userPrincipal) {
        return ResponseEntity.ok(ResponseDataAPI.builder()
                        .status(CommonConstant.SUCCESS)
                        .data(courseService.createCourse(courseRequest,userPrincipal.getId()))
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
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "paging", defaultValue = "5") int paging,
            @RequestParam(value = "sort", defaultValue = "created_at") String sort,
            @RequestParam(value = "order", defaultValue = "desc") String order,
            @Filter Specification<Course> specification) {
        Pageable pageable = PagingUtils.makePageRequest(sort, order, page, paging);
        Page<CourseResponse> coursesPage = courseService.coursePageResponse(pageable, specification);
        PageInfo pageInfo = new PageInfo(
                pageable.getPageNumber() + 1,
                coursesPage.getTotalPages(),
                coursesPage.getTotalElements()
        );
        return  ResponseEntity.ok(ResponseDataAPI.success(coursesPage.getContent(), pageInfo));
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



    @PatchMapping("/{courseId}/image")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
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
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<ResponseDataAPI> updateCourse(
            @PathVariable UUID courseId,
            @Valid @RequestBody CourseRequest courseRequest) {
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data(courseService.updateCourse(courseId, courseRequest))
                .build());
    }
    @DeleteMapping("/{courseId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<ResponseDataAPI> deleteCourse(@PathVariable UUID courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data("Course deleted successfully")
                .build());
    }
    // --- ADD TAGS TO COURSE ---
    @PutMapping("/{courseId}/tags")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<ResponseDataAPI> addTagsToCourse(
            @PathVariable UUID courseId,
            @RequestBody Set<UUID> tagIds) {
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data(courseService.addTagsToCourse(courseId, tagIds))
                .build());
    }
    @DeleteMapping("/{courseId}/tags/{tagId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
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

    /// use slug
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ResponseDataAPI> getCourseBySlug(
            @PathVariable("slug") String slug) {
        CourseResponse courseResponse = courseService.getCourseBySlug(slug);
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data(courseResponse)
                .build());
    }
    /// get detail by slug
    @GetMapping("/slug/{slug}/detail")
    public ResponseEntity<ResponseDataAPI> getCourseDetailBySlug(
            @PathVariable("slug") String slug) {
        CourseResponse courseResponse = courseService.getCourseDetailBySlug(slug);
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data(courseResponse)
                .build());
    };
    // Upload image by slug
    @PatchMapping("/slug/{slug}/image")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<ResponseDataAPI> uploadCourseImageBySlug(
            @PathVariable("slug") String slug,
            @RequestParam("imageUrl") String imageUrl) {
        String savedImageUrl = courseService.uploadCourseImageBySlug(slug, imageUrl);
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data(savedImageUrl)
                .build());
    }

    // Update course by slug
    @PutMapping("/slug/{slug}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<ResponseDataAPI> updateCourseBySlug(
            @PathVariable("slug") String slug,
            @Valid @RequestBody CourseRequest courseRequest) {
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data(courseService.updateCourseBySlug(slug, courseRequest))
                .build());
    }

    // Delete course by slug
    @DeleteMapping("/slug/{slug}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<ResponseDataAPI> deleteCourseBySlug(@PathVariable("slug") String slug) {
        courseService.deleteCourseBySlug(slug);
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data("Course deleted successfully")
                .build());
    }

    @GetMapping("/category")
    public ResponseEntity<ResponseDataAPI> getAllCategories() {
        return ResponseEntity.ok(ResponseDataAPI.builder()
                .status(CommonConstant.SUCCESS)
                .data(courseService.getAllCategories())
                .build());
    }
        @GetMapping("/instructor/{instructorId}")
        public ResponseEntity<ResponseDataAPI> getCoursesByInstructorId(@PathVariable UUID instructorId) {
                return ResponseEntity.ok(ResponseDataAPI.builder()
                                .status(CommonConstant.SUCCESS)
                                .data(courseService.getCoursesByInstructorId(instructorId))
                                .build());
        }

        @GetMapping("/with-instructor-info/{courseId}")
                public ResponseEntity<ResponseDataAPI> getCourseInstructorById(
                                @PathVariable("courseId") UUID courseId) {
                        CourseResponeInstructor courseResponse = courseService.getCourseInstructorById(courseId);

                        return ResponseEntity.ok(ResponseDataAPI.builder()
                                        .status(CommonConstant.SUCCESS)
                                        .data(courseResponse)
                                        .build());
                }
}