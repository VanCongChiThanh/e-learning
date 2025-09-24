package com.pbl.elearning.course.payload.request;

import com.pbl.elearning.course.domain.Lecture;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;

@Data
public class SectionRequest {
    private String title;
    private String description;
    private Integer position;
    List<Lecture> lectures;
    public SectionRequest() {
        this.position = 0;
    }


}
