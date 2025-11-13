package com.pbl.elearning.course;

import com.pbl.elearning.course.domain.Lecture;
import com.pbl.elearning.course.domain.Resource;
import com.pbl.elearning.course.payload.request.ResourceRequest;
import com.pbl.elearning.course.payload.response.ResourceResponse;
import com.pbl.elearning.course.repository.LectureRepository;
import com.pbl.elearning.course.repository.ResourceRepository;
import com.pbl.elearning.course.service.impl.ResourceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceImplTest {

    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private LectureRepository lectureRepository;

    @InjectMocks
    private ResourceServiceImpl resourceService;

    private UUID lectureId;
    private UUID resourceId;

    @BeforeEach
    void setUp() {
        lectureId = UUID.randomUUID();
        resourceId = UUID.randomUUID();
    }

    @Test
    void createResource_shouldReturnResourceResponse_whenLectureExists() {
        ResourceRequest req = ResourceRequest.builder()
                .fileUrl("http://file.pdf")
                .fileType("pdf")
                .build();

        Lecture lecture = Lecture.builder().lectureId(lectureId).build();

        when(lectureRepository.findById(lectureId)).thenReturn(Optional.of(lecture));
        when(resourceRepository.save(any(Resource.class))).thenAnswer(inv -> {
            Resource r = inv.getArgument(0);
            r.setResourceId(resourceId);
            return r;
        });

        ResourceResponse resp = resourceService.createResource(req, lectureId);

        assertNotNull(resp);
        assertEquals(resourceId, resp.getResourceId());
        assertEquals(lectureId, resp.getLectureId());
        assertEquals("http://file.pdf", resp.getFileURL());
        assertEquals("pdf", resp.getFileType());
        verify(resourceRepository, times(1)).save(any(Resource.class));
    }

    @Test
    void getResourceById_shouldReturn_whenFound() {
        Resource r = Resource.builder().resourceId(resourceId).fileUrl("u").fileType("pdf").build();
        r.setLecture(Lecture.builder().lectureId(lectureId).build());

        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(r));

        ResourceResponse resp = resourceService.getResourceById(resourceId);

        assertNotNull(resp);
        assertEquals(resourceId, resp.getResourceId());
        assertEquals("u", resp.getFileURL());
        assertEquals("pdf", resp.getFileType());
    }

    @Test
    void getResourceById_shouldThrow_whenNotFound() {
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> resourceService.getResourceById(resourceId));
    }

    @Test
    void updateResource_shouldModifyAndReturn() {
        Resource r = Resource.builder().resourceId(resourceId).fileUrl("old").fileType("txt").build();
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(r));
        when(resourceRepository.save(any(Resource.class))).thenAnswer(inv -> inv.getArgument(0));

        ResourceRequest req = ResourceRequest.builder().fileUrl("new").fileType("pdf").build();
        ResourceResponse resp = resourceService.updateResource(resourceId, req);

        assertNotNull(resp);
        assertEquals("new", resp.getFileURL());
        assertEquals("pdf", resp.getFileType());
        verify(resourceRepository, times(1)).save(any(Resource.class));
    }

    @Test
    void deleteResource_shouldCallDelete_whenFound() {
        Resource r = Resource.builder().resourceId(resourceId).fileUrl("x").fileType("pdf").build();
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(r));

        resourceService.deleteResource(resourceId);

        verify(resourceRepository, times(1)).delete(r);
    }

}
