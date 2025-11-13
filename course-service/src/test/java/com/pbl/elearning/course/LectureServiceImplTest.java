package com.pbl.elearning.course;

import com.pbl.elearning.course.domain.Course;
import com.pbl.elearning.course.domain.Lecture;
import com.pbl.elearning.course.domain.Section;
import com.pbl.elearning.course.payload.request.LectureRequest;
import com.pbl.elearning.course.payload.response.LectureResponse;
import com.pbl.elearning.course.repository.CourseRepository;
import com.pbl.elearning.course.repository.LectureRepository;
import com.pbl.elearning.course.repository.SectionRepository;
import com.pbl.elearning.course.service.impl.LectureServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LectureServiceImplTest {

    @Mock
    private SectionRepository sectionRepository;
    @Mock
    private LectureRepository lectureRepository;
    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private LectureServiceImpl lectureService;

    private UUID sectionId;
    private UUID lectureId;
    private UUID courseId;

    @BeforeEach
    void setUp() {
        sectionId = UUID.randomUUID();
        lectureId = UUID.randomUUID();
        courseId = UUID.randomUUID();
    }

    @Test
    void createLecture_shouldReturnLectureResponse_whenSectionExists() {
        LectureRequest req = LectureRequest.builder()
                .title("Lecture 1")
                .position(1)
                .videoUrl("http://video")
                .duration(120)
                .build();

        Section section = Section.builder()
                .sectionId(sectionId)
                .title("Sec")
                .build();

        when(sectionRepository.findById(sectionId)).thenReturn(Optional.of(section));
        when(lectureRepository.save(any(Lecture.class))).thenAnswer(inv -> {
            Lecture l = inv.getArgument(0);
            l.setLectureId(lectureId);
            return l;
        });

        LectureResponse resp = lectureService.createLecture(req, sectionId);

        assertNotNull(resp);
        assertEquals(lectureId, resp.getLectureId());
        assertEquals(sectionId, resp.getSectionId());
        assertEquals("Lecture 1", resp.getTitle());
        verify(lectureRepository, times(1)).save(any(Lecture.class));
    }

    @Test
    void getAllLecturesBySectionId_shouldReturnMappedResponses() {
        Lecture l = Lecture.builder()
                .lectureId(lectureId)
                .title("L1")
                .position(1)
                .build();
        l.setSection(Section.builder().sectionId(sectionId).build());

        when(lectureRepository.findBySection_SectionId(sectionId)).thenReturn(List.of(l));

        List<LectureResponse> resp = lectureService.getAllLecturesBySectionId(sectionId);

        assertNotNull(resp);
        assertEquals(1, resp.size());
        assertEquals(lectureId, resp.get(0).getLectureId());
    }

    @Test
    void getLectureById_shouldReturn_whenFound() {
        Lecture l = Lecture.builder()
                .lectureId(lectureId)
                .title("L1")
                .position(2)
                .build();
        when(lectureRepository.findById(lectureId)).thenReturn(Optional.of(l));

        LectureResponse resp = lectureService.getLectureById(lectureId);

        assertNotNull(resp);
        assertEquals(lectureId, resp.getLectureId());
        assertEquals("L1", resp.getTitle());
    }

    @Test
    void getLectureById_shouldThrow_whenNotFound() {
        when(lectureRepository.findById(lectureId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> lectureService.getLectureById(lectureId));
    }

    @Test
    void updateLecture_shouldModifyAndReturn() {
        Lecture l = Lecture.builder()
                .lectureId(lectureId)
                .title("Old")
                .position(1)
                .build();
        when(lectureRepository.findById(lectureId)).thenReturn(Optional.of(l));
        when(lectureRepository.save(any(Lecture.class))).thenAnswer(inv -> inv.getArgument(0));

        LectureRequest req = LectureRequest.builder().title("New").position(5).build();
        LectureResponse resp = lectureService.updateLecture(lectureId, req);

        assertNotNull(resp);
        assertEquals("New", resp.getTitle());
        assertEquals(5, resp.getPosition());
        verify(lectureRepository, times(1)).save(any(Lecture.class));
    }

    @Test
    void deleteLecture_shouldCallDelete_whenFound() {
        Lecture l = Lecture.builder().lectureId(lectureId).title("x").build();
        when(lectureRepository.findById(lectureId)).thenReturn(Optional.of(l));

        lectureService.deleteLecture(lectureId);

        verify(lectureRepository, times(1)).delete(l);
    }

    @Test
    void updateLectureVideo_shouldSetVideoUrl() {
        Lecture l = Lecture.builder().lectureId(lectureId).title("x").build();
        when(lectureRepository.findById(lectureId)).thenReturn(Optional.of(l));
        when(lectureRepository.save(any(Lecture.class))).thenAnswer(inv -> inv.getArgument(0));

        LectureResponse resp = lectureService.updateLectureVideo(lectureId, "http://v");

        assertNotNull(resp);
        assertEquals("http://v", resp.getVideoUrl());
        verify(lectureRepository, times(1)).save(any(Lecture.class));
    }

    @Test
    void countLectureByCourseId_shouldReturnCount() {
        Course course = Course.builder().courseId(courseId).build();
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(lectureRepository.countBySection_Course_CourseId(course.getCourseId())).thenReturn(7);

        Integer count = lectureService.countLectureByCourseId(courseId);

        assertEquals(7, count);
    }

}
