package com.pbl.elearning.commerce.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.UUID;
import java.util.List;

@Getter
@Setter
public class CreateOrderRequest {

    @NotEmpty(message = "Order items cannot be empty")
    @Valid
    private List<OrderItemRequest> items;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    @Getter
    @Setter
    public static class OrderItemRequest {

        @NotNull(message = "Course ID is required")
        private UUID courseId;

        @NotNull(message = "Course price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Course price must be greater than 0")
        private Double coursePrice;
    }
}
