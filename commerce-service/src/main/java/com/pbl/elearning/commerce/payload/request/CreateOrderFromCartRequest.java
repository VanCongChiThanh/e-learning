package com.pbl.elearning.commerce.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
public class CreateOrderFromCartRequest {

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    private Boolean clearCartAfterOrder = true;
}
