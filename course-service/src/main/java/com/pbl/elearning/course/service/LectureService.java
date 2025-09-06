package com.pbl.elearning.course.service;

import com.pbl.elearning.course.payload.request.LectureRequest;
import com.pbl.elearning.course.payload.response.LectureResponse;

import java.util.UUID;

public interface LectureService {
    LectureResponse createLecture(LectureRequest lectureRequest, UUID sectionId);
    LectureResponse getLectureById(UUID lectureId);
    LectureResponse updateLecture(UUID lectureId, LectureRequest lectureRequest);
    void deleteLecture(UUID lectureId);



}
