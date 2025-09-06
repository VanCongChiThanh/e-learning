package com.pbl.elearning.course.payload.request;

import com.pbl.elearning.course.domain.Tag;
import com.pbl.elearning.course.domain.enums.Category;
import com.pbl.elearning.course.domain.enums.CourseLevel;
import com.pbl.elearning.course.domain.enums.CourseStatus;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Data
public class CourseRequest {
    @NotBlank private String title;

    private String description;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal price= BigDecimal.ZERO;

    private CourseStatus courseStatus= CourseStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    private CourseLevel level;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Category category;

    private String image;


    private Set<Tag> tags;

    private List<SectionRequest> sections;


}
