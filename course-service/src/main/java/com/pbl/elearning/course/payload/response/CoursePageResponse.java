package com.pbl.elearning.course.payload.response;

import com.pbl.elearning.common.payload.general.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CoursePageResponse {
    private List<CourseResponse> courses;
    private PageInfo pageInfo;
}
