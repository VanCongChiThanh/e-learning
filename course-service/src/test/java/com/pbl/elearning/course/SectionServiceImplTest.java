package com.pbl.elearning.course;

import com.pbl.elearning.course.domain.Course;
import com.pbl.elearning.course.domain.Section;
import com.pbl.elearning.course.payload.request.SectionRequest;
import com.pbl.elearning.course.payload.response.SectionResponse;
import com.pbl.elearning.course.repository.CourseRepository;
import com.pbl.elearning.course.repository.SectionRepository;
import com.pbl.elearning.course.service.impl.SectionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SectionServiceImplTest {

    @Mock
    private SectionRepository sectionRepository;
    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private SectionServiceImpl sectionService;

    private UUID courseId;
    private UUID sectionId;

    @BeforeEach
    void setUp() {
        courseId = UUID.randomUUID();
        sectionId = UUID.randomUUID();
    }
   //commented out because createSection method is not implemented yet
//    @Test
//    void createSection_shouldReturnSectionResponse_whenCourseExists() {
//        SectionRequest req = new SectionRequest();
//        req.setTitle("Section A");
//        req.setPosition(3);
//
//        Course course = Course.builder().courseId(courseId).build();
//        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
//
//        SectionResponse resp = sectionService.createSection(req, courseId);
//
//        assertNotNull(resp);
//        assertEquals("Section A", resp.getTitle());
//        assertEquals(3, resp.getPosition());
//        assertEquals(courseId, resp.getCourseId());
//    }

    @Test
    void getSectionById_shouldReturn_whenFound() {
        Section s = Section.builder().sectionId(sectionId).title("S1").position(1).build();
        when(sectionRepository.findById(sectionId)).thenReturn(Optional.of(s));

        SectionResponse resp = sectionService.getSectionById(sectionId);

        assertNotNull(resp);
        assertEquals(sectionId, resp.getSectionId());
        assertEquals("S1", resp.getTitle());
    }

    @Test
    void getSectionById_shouldThrow_whenNotFound() {
        when(sectionRepository.findById(sectionId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> sectionService.getSectionById(sectionId));
    }

    @Test
    void getAllSections_shouldReturnList() {
        Section s = Section.builder().sectionId(sectionId).title("S1").position(1).build();
        when(sectionRepository.findByCourse_CourseId(courseId)).thenReturn(List.of(s));

        List<SectionResponse> resp = sectionService.getAllSections(courseId);

        assertNotNull(resp);
        assertEquals(1, resp.size());
        assertEquals("S1", resp.get(0).getTitle());
    }

    @Test
    void updateSection_shouldModifyAndReturn() {
        Section s = Section.builder().sectionId(sectionId).title("Old").position(1).build();
        when(sectionRepository.findById(sectionId)).thenReturn(Optional.of(s));
        when(sectionRepository.save(any(Section.class))).thenAnswer(inv -> inv.getArgument(0));

        SectionRequest req = new SectionRequest();
        req.setTitle("New");
        req.setPosition(5);

        SectionResponse resp = sectionService.updateSection(sectionId, req);

        assertNotNull(resp);
        assertEquals("New", resp.getTitle());
        assertEquals(5, resp.getPosition());
        verify(sectionRepository, times(1)).save(any(Section.class));
    }

    @Test
    void deleteSection_shouldCallDelete_whenFound() {
        Section s = Section.builder().sectionId(sectionId).title("Del").build();
        when(sectionRepository.findById(sectionId)).thenReturn(Optional.of(s));

        sectionService.deleteSection(sectionId);

        verify(sectionRepository, times(1)).delete(s);
    }

}