package com.pbl.elearning.course.payload.response;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pbl.elearning.course.domain.enums.Category;
import com.pbl.elearning.course.domain.enums.CourseLevel;
import com.pbl.elearning.course.domain.enums.CourseStatus;
import com.pbl.elearning.user.payload.response.UserInfoResponse;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseResponeInstructor {
    private UUID courseId;
    private String title;
    private String slug;
    private String description;
    private BigDecimal price;
    @Enumerated(EnumType.STRING)
    private CourseStatus status;
    @Enumerated(EnumType.STRING)
    private CourseLevel level;
    private UserInfoResponse instructor;
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

}
