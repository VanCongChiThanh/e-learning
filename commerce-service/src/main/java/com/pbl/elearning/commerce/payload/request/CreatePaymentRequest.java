package com.pbl.elearning.commerce.payload.request;

import com.pbl.elearning.commerce.domain.enums.PaymentMethod;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.*;

@Getter
@Setter
public class CreatePaymentRequest {

    @NotNull(message = "Order ID is required")
    private java.util.UUID orderId;

    private PaymentMethod paymentMethod = PaymentMethod.PAYOS;

    @Size(max = 25, message = "Description cannot exceed 25 characters")
    private String description = "Coursevo";

    @Size(max = 200, message = "Return URL cannot exceed 200 characters")
    private String returnUrl;

    @Size(max = 200, message = "Cancel URL cannot exceed 200 characters")
    private String cancelUrl;

    // Additional PayOS specific fields
    @Min(value = 1, message = "Expiration time must be at least 1 minute")
    @Max(value = 43200, message = "Expiration time cannot exceed 43200 minutes (30 days)")
    private Integer expirationMinutes = 15; // Default 15 minutes
}
