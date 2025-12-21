package com.pbl.elearning.course.service.impl;

import com.github.slugify.Slugify;
import com.pbl.elearning.common.constant.MessageConstant;
import com.pbl.elearning.common.exception.NotFoundException;
import com.pbl.elearning.course.domain.Course;
import com.pbl.elearning.course.domain.Tag;
import com.pbl.elearning.course.domain.enums.Category;
import com.pbl.elearning.course.domain.enums.CourseStatus;
import com.pbl.elearning.course.payload.request.CourseRequest;
import com.pbl.elearning.course.payload.response.CourseResponeInstructor;
import com.pbl.elearning.course.payload.response.CourseResponse;
import com.pbl.elearning.course.payload.response.TagResponse;
import com.pbl.elearning.course.repository.CourseRepository;
import com.pbl.elearning.course.repository.TagRepository;
import com.pbl.elearning.course.service.CourseService;
import com.pbl.elearning.course.service.LectureService;
import com.pbl.elearning.course.service.ReviewService;
import com.pbl.elearning.course.service.TagService;
import com.pbl.elearning.user.domain.UserInfo;
import com.pbl.elearning.user.payload.response.UserInfoResponse;
import com.pbl.elearning.user.repository.UserInfoRepository;
import com.pbl.elearning.user.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
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
        private final Slugify slugify = Slugify.builder().build();
        private final TagService tagService;
        private final ReviewService reviewService;
        private final LectureService lectureService;

        private final UserInfoService userInfoService;
        private final UserInfoRepository userInfoRepository;

        @Override
        public CourseResponse createCourse(CourseRequest courseRequest, UUID instructorId) {
                String slug = slugify.slugify(courseRequest.getTitle());

                Course course = Course.builder()
                                .title(courseRequest.getTitle())
                                .slug(slug)
                                .description(courseRequest.getDescription())
                                .price(courseRequest.getPrice())
                                .level(courseRequest.getLevel())
                                .category(courseRequest.getCategory())
                                .image(courseRequest.getImage())
                                .instructorId(instructorId)

                                .build();

                Course savedCourse = courseRepository.save(course);
                return CourseResponse.builder()
                                .courseId(savedCourse.getCourseId())
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
                                                .slug(course.getSlug())
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
        public Page<CourseResponse> coursePageResponse(Pageable pageable, Specification<Course> spec) {
                Page<Course> coursePage;
                Specification<Course> publicSpec = (root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("courseStatus"), CourseStatus.PUBLISHED);
                Specification<Course> finalSpec = Specification.where(publicSpec).and(spec);

                coursePage = courseRepository.findAll(finalSpec, pageable);
                return coursePage.map(course -> {
                        String instructorName = userInfoRepository.findByUserId(course.getInstructorId())
                                        .map(user -> user.getFirstName() + " " + user.getLastName())
                                        .orElse("Unknown Instructor");

                        return CourseResponse.toCourseResponse_instructor(course, instructorName);
                });
        }

        @Override
        public Page<CourseResponeInstructor> coursePageResponseV2(Pageable pageable, Specification<Course> spec) {
                Page<Course> coursePage;

                coursePage = courseRepository.findAll(spec, pageable);
                return coursePage.map(course -> getCourseInstructorById(course.getCourseId()));
        }

        @Override
        public CourseResponse getCourseById(UUID courseId) {
                Course course = courseRepository.findById(courseId).orElseThrow(
                                () -> new NotFoundException(MessageConstant.NOT_FOUND));
                return CourseResponse.toCourseResponse(course);
        }

        @Override
        public String uploadCourseImage(UUID courseId, String urlfile) {
                Course course = courseRepository.findById(courseId).orElseThrow(
                                () -> new NotFoundException(MessageConstant.NOT_FOUND));
                course.setImage(urlfile);
                courseRepository.save(course);
                return urlfile;
        }

        @Override
        public CourseResponse updateCourse(UUID courseId, CourseRequest courseRequest) {
                Course course = courseRepository.findById(courseId).orElseThrow(
                                () -> new NotFoundException(MessageConstant.NOT_FOUND));
                course.setTitle(courseRequest.getTitle());
                course.setDescription(courseRequest.getDescription());
                course.setPrice(courseRequest.getPrice());
                course.setLevel(courseRequest.getLevel());
                course.setCategory(courseRequest.getCategory());
                Course updatedCourse = courseRepository.save(course);
                return CourseResponse.toCourseResponse(updatedCourse);
        }

        @Override
        public void deleteCourse(UUID courseId) {
                Course course = courseRepository.findById(courseId).orElseThrow(
                                () -> new NotFoundException(MessageConstant.NOT_FOUND));
                courseRepository.delete(course);

        }

        // implement course_tag methods

        @Override
        public CourseResponse addTagsToCourse(UUID courseId, Set<UUID> tagIds) {
                Course course = courseRepository.findById(courseId)
                                .orElseThrow(() -> new NotFoundException("Course not found with id: " + courseId));
                List<Tag> foundTags = tagRepository.findAllById(tagIds);
                if (foundTags.isEmpty()) {
                        throw new NotFoundException("No tags found with the provided IDs.");
                }
                Set<Tag> tagsToAdd = new HashSet<>(foundTags);
                if (course.getTags() == null) {
                        course.setTags(new HashSet<>());
                }
                course.getTags().addAll(tagsToAdd);
                Course updatedCourse = courseRepository.save(course);
                return CourseResponse.toCourseResponse(updatedCourse);
        }

        @Override
        public CourseResponse removeTagFromCourse(UUID courseId, UUID tagId) {
                Course course = courseRepository.findById(courseId).orElseThrow(
                                () -> new EntityNotFoundException("Course not found with id: " + courseId));
                Tag tag = tagRepository.findById(tagId)
                                .orElseThrow(() -> new EntityNotFoundException("Tag not found with id: " + tagId));
                course.getTags().remove(tag);
                Course updatedCourse = courseRepository.save(course);
                return CourseResponse.toCourseResponse(updatedCourse);
        }

        @Override
        public Set<TagResponse> getTagsByCourseId(UUID courseId) {
                Course course = courseRepository.findById(courseId).orElseThrow(
                                () -> new EntityNotFoundException("Course not found with id: " + courseId));
                Set<Tag> tags = course.getTags();
                return tags.stream()
                                .map(TagResponse::fromEntity)
                                .collect(java.util.stream.Collectors.toSet());
        }

        /// use slug
        @Override
        public CourseResponse getCourseBySlug(String slug) {
                Course course = courseRepository.findBySlug(slug)
                                .orElseThrow(() -> new EntityNotFoundException("Course not found with slug: " + slug));
                return CourseResponse.toCourseResponse(course);
        }

        @Override
        public String uploadCourseImageBySlug(String slug, String urlfile) {
                Course course = courseRepository.findBySlug(slug)
                                .orElseThrow(() -> new EntityNotFoundException("Course not found with slug: " + slug));
                course.setImage(urlfile);
                courseRepository.save(course);
                return urlfile;
        }

        @Override
        public CourseResponse updateCourseBySlug(String slug, CourseRequest courseRequest) {
                Course course = courseRepository.findBySlug(slug)
                                .orElseThrow(() -> new EntityNotFoundException("Course not found with slug: " + slug));
                course.setTitle(courseRequest.getTitle());
                course.setDescription(courseRequest.getDescription());
                course.setPrice(courseRequest.getPrice());
                course.setLevel(courseRequest.getLevel());
                course.setCategory(courseRequest.getCategory());
                Course updatedCourse = courseRepository.save(course);
                return CourseResponse.toCourseResponse(updatedCourse);
        }

        @Override
        public CourseResponse getCourseDetailBySlug(String slug) {
                Course course = courseRepository.findBySlug(slug)
                                .orElseThrow(() -> new EntityNotFoundException("Course not found with slug: " + slug));
                String instructorName = userInfoRepository.findByUserId(course.getInstructorId())
                                .map(user -> user.getFirstName() + " " + user.getLastName())
                                .orElse("Unknown Instructor");
                Set<TagResponse> tags = tagService.getTagsByCourseId(course.getCourseId());
                Double avgRating = reviewService.getAverageRatingByCourseId(course.getCourseId());
                Integer totalReviews = reviewService.getTotalReviewsByCourseId(course.getCourseId());
                Integer totalLectures = lectureService.countLectureByCourseId(course.getCourseId());
                return CourseResponse.toCourseDetailResponse(
                                course, tags, avgRating, totalReviews, totalLectures, course.getTotalStudents(), instructorName);

        }

        @Override
        public void deleteCourseBySlug(String slug) {
                Course course = courseRepository.findBySlug(slug)
                                .orElseThrow(() -> new EntityNotFoundException("Course not found with slug: " + slug));
                courseRepository.delete(course);
        }

        @Override
        public List<Category> getAllCategories() {
                return List.of(Category.values());
        }

        @Override
        public List<CourseResponse> getCoursesByInstructorId(UUID instructorId) {
                List<Course> courses = courseRepository.findByInstructorId(instructorId);
                return courses.stream()
                                .map(CourseResponse::toCourseResponse)
                                .toList();
        }

        @Override
        public CourseResponeInstructor getCourseInstructorById(UUID courseId) {
                Course course = courseRepository.findById(courseId).orElseThrow(
                                () -> new EntityNotFoundException("Course not found with id: " + courseId));
                UserInfo userInfo = userInfoService.getUserInfoByUserId(course.getInstructorId());
                UserInfoResponse instructorResponse = UserInfoResponse.toResponse(userInfo);
                return CourseResponeInstructor.builder()
                                .courseId(course.getCourseId())
                                .title(course.getTitle())
                                .slug(course.getSlug())
                                .description(course.getDescription())
                                .price(course.getPrice())
                                .status(course.getCourseStatus())
                                .level(course.getLevel())
                                .instructor(instructorResponse)
                                .category(course.getCategory())
                                .image(course.getImage())
                                .createdAt(course.getCreatedAt())
                                .deletedAt(course.getDeletedAt())
                                .build();
        }

        @Override
        public List<CourseResponse> getCoursesByListIds(List<UUID> courseIds) {
                List<Course> courses = courseRepository.findAllById(courseIds);
                return courses.stream()
                                .map(CourseResponse::toCourseResponse)
                                .toList();
        }

        @Override
        public CourseResponse getCourseDetailForInstructor(UUID courseId, UUID instructorId) {
            Course course = courseRepository.findByCourseIdAndInstructorId(courseId, instructorId)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Course not found with id: " + courseId + " for instructor: " + instructorId));
            return CourseResponse.toCourseResponse(course);
        }

        @Override
        public Page<CourseResponse> getCoursesByInstructor(UUID instructorId, Specification<Course> spec, Pageable pageable) {
            Specification<Course> instructorSpec = (root, query, cb) ->
                    cb.equal(root.get("instructorId"), instructorId);

            Specification<Course> finalSpec = (spec == null)
                    ? instructorSpec
                    : Specification.where(spec).and(instructorSpec);

            Page<Course> coursePage = courseRepository.findAll(finalSpec, pageable);

            return coursePage.map(CourseResponse::toCourseResponse);
        }




}