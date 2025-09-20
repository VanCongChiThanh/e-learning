package com.pbl.elearning.commerce.payload.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ApplyCouponRequest {

    @NotBlank(message = "Coupon code is required")
    @Size(max = 100, message = "Coupon code cannot exceed 100 characters")
    private String couponCode;
}
