package com.pbl.elearning.commerce.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.util.UUID;
import java.math.BigDecimal;

@Getter
@Setter
public class AddToCartRequest {

    @NotNull(message = "Course ID is required")
    private UUID courseId;

    @NotNull(message = "Course price is required")
    private BigDecimal addedPrice;

}
