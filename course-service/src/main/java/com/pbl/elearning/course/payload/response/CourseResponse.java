package com.pbl.elearning.course.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseResponse {
    private UUID courseId;
    private String title;
    private String slug;
    private String description;
    private BigDecimal price;
    @Enumerated(EnumType.STRING)
    private CourseStatus status;
    @Enumerated(EnumType.STRING)
    private CourseLevel level;
    private UUID instructorId;
    private String instructorName;
    @Enumerated(EnumType.STRING)
    private Category category;
    private String image;
    private Set<TagResponse> tags;
    private Double averageRating;
    private  Integer totalReviews;
    private  Integer totalLectures;
    private Integer totalStudents;
    private Timestamp createdAt;
    private Timestamp deletedAt;

    public static CourseResponse toCourseResponse (Course course){

        return CourseResponse.builder()
                .courseId(course.getCourseId())
                .title(course.getTitle())
                .slug(course.getSlug())
                .description(course.getDescription())
                .price(course.getPrice())
                .status(course.getCourseStatus())
                .level(course.getLevel())
                .instructorId(course.getInstructorId())
                .category(course.getCategory())
                .image(course.getImage())
                .createdAt(course.getCreatedAt())
                .deletedAt(course.getDeletedAt())
                .averageRating(course.getAverageRating() != null ? course.getAverageRating() : 0.0)
                .totalReviews(course.getTotalReviews() != null ? course.getTotalReviews() : 0)
                .totalStudents(course.getTotalStudents() != null ? course.getTotalStudents() : 0)
                .build();
    }

    public static CourseResponse toCourseResponse_instructor (Course course, String instructorName){

        return CourseResponse.builder()
                .courseId(course.getCourseId())
                .title(course.getTitle())
                .slug(course.getSlug())
                .description(course.getDescription())
                .price(course.getPrice())
                .status(course.getCourseStatus())
                .level(course.getLevel())
                .instructorId(course.getInstructorId())
                .instructorName(instructorName)
                .category(course.getCategory())
                .image(course.getImage())
                .createdAt(course.getCreatedAt())
                .deletedAt(course.getDeletedAt())
                .averageRating(course.getAverageRating() != null ? course.getAverageRating() : 0.0)
                .totalReviews(course.getTotalReviews() != null ? course.getTotalReviews() : 0)
                .totalStudents(course.getTotalStudents() != null ? course.getTotalStudents() : 0)
                .build();
    }


    // detail response
    public static CourseResponse toCourseDetailResponse(
            Course course,
            Set<TagResponse> tags,
            Double averageRating,
            Integer totalReviews,
            Integer totalLectures,
            Integer totalStudents,
            String instructorName
    ) {
        return CourseResponse.builder()
                .courseId(course.getCourseId())
                .title(course.getTitle())
                .slug(course.getSlug())
                .description(course.getDescription())
                .price(course.getPrice())
                .status(course.getCourseStatus())
                .level(course.getLevel())
                .instructorId(course.getInstructorId())
                .instructorName(instructorName)
                .category(course.getCategory())
                .image(course.getImage())
                .createdAt(course.getCreatedAt())
                .deletedAt(course.getDeletedAt())
                // thêm dữ liệu chi tiết
                .tags(tags)
                .averageRating(averageRating)
                .totalReviews(totalReviews)
                .totalLectures(totalLectures)
                .totalStudents(totalStudents)
                .build();
    }
}
