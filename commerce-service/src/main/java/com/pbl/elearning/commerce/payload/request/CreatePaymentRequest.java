package com.pbl.elearning.commerce.payload.request;

import com.pbl.elearning.commerce.domain.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
public class CreatePaymentRequest {

    @NotNull(message = "Order ID is required")
    @Positive(message = "Order ID must be positive")
    private Long orderId;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod = PaymentMethod.PAYOS;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @Size(max = 200, message = "Return URL cannot exceed 200 characters")
    private String returnUrl;

    @Size(max = 200, message = "Cancel URL cannot exceed 200 characters")
    private String cancelUrl;

    // Additional PayOS specific fields
    @Min(value = 1, message = "Expiration time must be at least 1 minute")
    @Max(value = 43200, message = "Expiration time cannot exceed 43200 minutes (30 days)")
    private Integer expirationMinutes = 15; // Default 15 minutes

    private String buyerName;

    @Email(message = "Invalid email format")
    private String buyerEmail;

    @Pattern(regexp = "^[0-9+\\-\\s()]+$", message = "Invalid phone number format")
    private String buyerPhone;

    private String buyerAddress;
}
