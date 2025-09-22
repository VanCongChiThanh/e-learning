package com.pbl.elearning.commerce.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@Getter
@Setter
public class AddToCartRequest {

    @NotNull(message = "Course ID is required")
    @Positive(message = "Course ID must be positive")
    private Long courseId;

    @NotBlank(message = "Course name is required")
    @Size(max = 255, message = "Course name cannot exceed 255 characters")
    private String courseName;

    @NotNull(message = "Course price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Course price must be greater than 0")
    private BigDecimal coursePrice;

    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 10, message = "Quantity cannot exceed 10")
    private Integer quantity = 1;

    @Size(max = 1000, message = "Course description cannot exceed 1000 characters")
    private String courseDescription;

    @Size(max = 500, message = "Course thumbnail URL cannot exceed 500 characters")
    private String courseThumbnail;

    @Size(max = 255, message = "Instructor name cannot exceed 255 characters")
    private String instructorName;

    @Positive(message = "Instructor ID must be positive")
    private Long instructorId;

    @Min(value = 0, message = "Course duration must be non-negative")
    private Integer courseDurationMinutes;

    @Size(max = 50, message = "Course level cannot exceed 50 characters")
    private String courseLevel;

    @Size(max = 100, message = "Course category cannot exceed 100 characters")
    private String courseCategory;

    @DecimalMin(value = "0.0", message = "Original price must be non-negative")
    private BigDecimal originalPrice;

    @DecimalMin(value = "0.0", message = "Discount amount must be non-negative")
    private BigDecimal discountAmount;

    @Min(value = 0, message = "Discount percentage must be non-negative")
    @Max(value = 100, message = "Discount percentage cannot exceed 100")
    private Integer discountPercentage;
}
