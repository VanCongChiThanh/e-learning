package com.pbl.elearning.course.service.impl;

import com.pbl.elearning.course.domain.Course;
import com.pbl.elearning.course.domain.Lecture;
import com.pbl.elearning.course.domain.Section;
import com.pbl.elearning.course.payload.request.LectureRequest;
import com.pbl.elearning.course.payload.response.LectureResponse;
import com.pbl.elearning.course.payload.response.SectionResponse;
import com.pbl.elearning.course.repository.CourseRepository;
import com.pbl.elearning.course.repository.LectureRepository;
import com.pbl.elearning.course.repository.SectionRepository;
import com.pbl.elearning.course.service.LectureService;
import com.pbl.elearning.course.service.SectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class LectureServiceImpl implements LectureService {
    private final SectionRepository sectionRepository;
    private final LectureRepository lectureRepository;
    private final CourseRepository courseRepository;

    @Override
    public LectureResponse createLecture(LectureRequest lectureRequest, UUID sectionId) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() ->
                        new RuntimeException("Section not found with id: " + sectionId));
        Lecture lecture= Lecture.builder()
                .title(lectureRequest.getTitle())
                .position(lectureRequest.getPosition())
                .section(section)
                .type(lectureRequest.getType())          // Bắt buộc vì DB not null
                .sourceUrl(lectureRequest.getSourceUrl()) // Map url video/tài liệu
                .duration(lectureRequest.getDuration())
                .build();
        Lecture savedLecture = lectureRepository.save(lecture);
        return LectureResponse.fromEntity(savedLecture);
    }

    @Override
    public List<LectureResponse> getAllLecturesBySectionId(UUID sectionId) {
        List<Lecture> lectures = lectureRepository.findBySection_SectionId(sectionId);
        return lectures.stream()
                .map(LectureResponse::fromEntity)
                .toList();
    }

    @Override
    public  LectureResponse getLectureById(UUID lectureId){
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() ->
                        new RuntimeException("Lecture not found with id: " + lectureId));
        return LectureResponse.fromEntity(lecture);
    }
    @Override
    public LectureResponse updateLecture(UUID lectureId, LectureRequest lectureRequest) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() ->
                        new RuntimeException("Lecture not found with id: " + lectureId));
        lecture.setTitle(lectureRequest.getTitle());
        lecture.setPosition(lectureRequest.getPosition());
        Lecture updatedLecture = lectureRepository.save(lecture);
        return LectureResponse.fromEntity(updatedLecture);
    }
    @Override
    public void deleteLecture(UUID lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() ->
                        new RuntimeException("Lecture not found with id: " + lectureId));
        lectureRepository.delete(lecture);
    }

    /// update video
    @Override
    public LectureResponse updateLectureVideo(UUID lectureId, String videoUrl) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() ->
                        new RuntimeException("Lecture not found with id: " + lectureId));
        lecture.setSourceUrl(videoUrl);
        Lecture updatedLecture = lectureRepository.save(lecture);
        return LectureResponse.fromEntity(updatedLecture);
    }

    @Override
    public Integer countLectureByCourseId(UUID courseId){
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new RuntimeException("Course not found with id: " + courseId));
        return lectureRepository.countBySection_Course_CourseId(course.getCourseId());

    }


}