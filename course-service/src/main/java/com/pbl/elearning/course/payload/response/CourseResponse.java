package com.pbl.elearning.course.payload.response;

import com.pbl.elearning.course.domain.Course;
import com.pbl.elearning.course.domain.enums.Category;
import com.pbl.elearning.course.domain.enums.CourseLevel;
import com.pbl.elearning.course.domain.enums.CourseStatus;
import lombok.Builder;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class CourseResponse {
    private UUID courseId;
    private String title;
    private String description;
    private BigDecimal price;
    @Enumerated(EnumType.STRING)
    private CourseStatus status;
    @Enumerated(EnumType.STRING)
    private CourseLevel level;
    private UUID instructorId;
    @Enumerated(EnumType.STRING)
    private Category category;
    private String image;
    private Set<TagResponse> tags;
    private Double averageRating;
    private  Integer totalReviews;
    private  Integer totalLectures;
    private Timestamp createdAt;
    private Timestamp deletedAt;

    public static CourseResponse toCourseResponse (Course course){

        return CourseResponse.builder()
                .courseId(course.getCourseId())
                .title(course.getTitle())
                .description(course.getDescription())
                .price(course.getPrice())
                .status(course.getCourseStatus())
                .level(course.getLevel())
                .instructorId(course.getInstructorId())
                .category(course.getCategory())
                .image(course.getImage())
                .createdAt(course.getCreatedAt())
                .deletedAt(course.getDeletedAt())
                .build();
    }
}
