package com.pbl.elearning.course.payload.request;

import com.pbl.elearning.course.domain.Resource;
import com.pbl.elearning.course.domain.enums.LectureType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LectureRequest {
    @NotBlank private String title;


    private Integer position;

    private String videoUrl;

    private Integer duration;

    Set<Resource> resources;
    
    @NotNull
    private LectureType type; 
}
