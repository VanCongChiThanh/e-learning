package com.pbl.elearning.course;

import com.pbl.elearning.common.exception.NotFoundException;
import com.pbl.elearning.course.domain.Course;
import com.pbl.elearning.course.domain.Tag;
import com.pbl.elearning.course.payload.request.CourseRequest;
import com.pbl.elearning.course.payload.response.CourseResponse;
import com.pbl.elearning.course.payload.response.TagResponse;
import com.pbl.elearning.course.repository.CourseRepository;
import com.pbl.elearning.course.repository.TagRepository;
import com.pbl.elearning.course.service.LectureService;
import com.pbl.elearning.course.service.ReviewService;
import com.pbl.elearning.course.service.TagService;
import com.pbl.elearning.course.service.impl.CourseServiceImpl;
import com.pbl.elearning.user.domain.UserInfo;
import com.pbl.elearning.user.repository.UserInfoRepository;
import org.hibernate.validator.internal.constraintvalidators.bv.notempty.NotEmptyValidatorForArray;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseServiceImplTest {

    @Mock
    private CourseRepository courseRepository;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private TagService tagService;
    @Mock
    private ReviewService reviewService;
    @Mock
    private LectureService lectureService;
    @Mock
    private UserInfoRepository userInfoRepository;

    @InjectMocks
    private CourseServiceImpl courseService;

    private UUID instructorId;

    @BeforeEach
    void setUp() {
        instructorId = UUID.randomUUID();
    }

    @Test
    void createCourse_shouldReturnCourseResponse_whenValidRequest() {
        CourseRequest req = new CourseRequest();
        req.setTitle("My Course Title");
        req.setDescription("Description here");
        req.setPrice(new BigDecimal("9.99"));
        req.setCategory(null);

        when(courseRepository.save(any(Course.class))).thenAnswer(inv -> {
            Course c = inv.getArgument(0);
            c.setCourseId(UUID.randomUUID());
            return c;
        });

        CourseResponse resp = courseService.createCourse(req, instructorId);

        assertNotNull(resp);
        assertEquals("My Course Title", resp.getTitle());
        assertEquals(instructorId, resp.getInstructorId());
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    void getCourseById_shouldReturnCourseResponse_whenFound() {
        UUID courseId = UUID.randomUUID();
        Course c = Course.builder()
                .courseId(courseId)
                .title("C1")
                .slug("c1")
                .description("d")
                .price(new BigDecimal("0"))
                .instructorId(instructorId)
                .build();

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(c));

        CourseResponse resp = courseService.getCourseById(courseId);

        assertNotNull(resp);
        assertEquals(courseId, resp.getCourseId());
        assertEquals("C1", resp.getTitle());
    }

    @Test
    void getCourseById_shouldThrow_whenNotFound() {
        UUID courseId = UUID.randomUUID();
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> courseService.getCourseById(courseId));
    }

    @Test
    void coursePageResponse_shouldMapInstructorName() {
        Course c = Course.builder()
                .courseId(UUID.randomUUID())
                .title("C1")
                .slug("c1")
                .instructorId(instructorId)
                .build();

        Page<Course> page = new PageImpl<>(List.of(c));
        Pageable pageable = PageRequest.of(0, 10);

        when(courseRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        UserInfo ui = new UserInfo();
        ui.setFirstName("John");
        ui.setLastName("Doe");
        when(userInfoRepository.findByUserId(instructorId)).thenReturn(Optional.of(ui));

        Page<CourseResponse> resp = courseService.coursePageResponse(pageable, (root, query, cb) -> null);

        assertNotNull(resp);
        assertEquals(1, resp.getContent().size());
        assertEquals("John Doe", resp.getContent().get(0).getInstructorName());
    }

    @Test
    void getCourseDetailBySlug_shouldReturnDetail_withTagsAndStats() {
        String slug = "my-slug";
        UUID courseId = UUID.randomUUID();
        Course c = Course.builder()
                .courseId(courseId)
                .slug(slug)
                .title("Course X")
                .instructorId(instructorId)
                .build();

        when(courseRepository.findBySlug(slug)).thenReturn(Optional.of(c));

        TagResponse tr = TagResponse.builder().name("tag1").tagId(UUID.randomUUID()).build();
        when(tagService.getTagsByCourseId(courseId)).thenReturn(Set.of(tr));
        when(reviewService.getAverageRatingByCourseId(courseId)).thenReturn(4.2);
        when(reviewService.getTotalReviewsByCourseId(courseId)).thenReturn(5);
        when(lectureService.countLectureByCourseId(courseId)).thenReturn(3);

        UserInfo ui = new UserInfo();
        ui.setFirstName("Jane");
        ui.setLastName("Smith");
        when(userInfoRepository.findByUserId(instructorId)).thenReturn(Optional.of(ui));

        CourseResponse resp = courseService.getCourseDetailBySlug(slug);

        assertNotNull(resp);
        assertEquals(courseId, resp.getCourseId());
        assertEquals(1, resp.getTags().size());
        assertEquals(4.2, resp.getAverageRating());
        assertEquals(5, resp.getTotalReviews());
        assertEquals(3, resp.getTotalLectures());
        assertEquals("Jane Smith", resp.getInstructorName());
    }

    @Test
    void addTagsToCourse_shouldAddTagsAndReturnCourse() {
        UUID courseId = UUID.randomUUID();
        Course c = Course.builder()
                .courseId(courseId)
                .title("With tags")
                .instructorId(instructorId)
                .build();

        UUID tagId = UUID.randomUUID();
        Tag tag = Tag.builder().tagId(tagId).name("t1").build();

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(c));
        when(tagRepository.findAllById(anyCollection())).thenReturn(List.of(tag));
        when(courseRepository.save(any(Course.class))).thenAnswer(inv -> inv.getArgument(0));

        var resp = courseService.addTagsToCourse(courseId, Set.of(tagId));

        assertNotNull(resp);
        assertEquals(courseId, resp.getCourseId());
        // ensure tags were added to entity
        assertTrue(c.getTags().stream().anyMatch(t -> t.getTagId().equals(tagId)));
        verify(courseRepository, times(1)).save(c);
    }

}