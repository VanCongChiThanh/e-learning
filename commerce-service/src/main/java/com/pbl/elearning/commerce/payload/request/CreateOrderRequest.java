package com.pbl.elearning.commerce.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;

@Getter
@Setter
public class CreateOrderRequest {

    @NotEmpty(message = "Order items cannot be empty")
    @Valid
    private List<OrderItemRequest> items;

    @Size(max = 100, message = "Coupon code cannot exceed 100 characters")
    private String couponCode;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    @Getter
    @Setter
    public static class OrderItemRequest {

        @NotNull(message = "Course ID is required")
        @Positive(message = "Course ID must be positive")
        private Long courseId;

        @NotBlank(message = "Course name is required")
        @Size(max = 255, message = "Course name cannot exceed 255 characters")
        private String courseName;

        @NotNull(message = "Course price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Course price must be greater than 0")
        private Double coursePrice;

        @Min(value = 1, message = "Quantity must be at least 1")
        @Max(value = 10, message = "Quantity cannot exceed 10")
        private Integer quantity = 1;

        @Size(max = 1000, message = "Course description cannot exceed 1000 characters")
        private String courseDescription;

        @Size(max = 500, message = "Course thumbnail URL cannot exceed 500 characters")
        private String courseThumbnail;

        @Size(max = 255, message = "Instructor name cannot exceed 255 characters")
        private String instructorName;
    }
}
