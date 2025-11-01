package com.pbl.elearning.web.endpoint.commerce;

import com.pbl.elearning.commerce.payload.request.CreateOrderFromCartRequest;
import com.pbl.elearning.commerce.payload.request.CreateOrderRequest;
import com.pbl.elearning.commerce.payload.response.OrderResponse;
import com.pbl.elearning.commerce.service.OrderService;
import com.pbl.elearning.common.payload.general.ResponseDataAPI;
import com.pbl.elearning.security.annotation.CurrentUser;
import com.pbl.elearning.security.domain.UserPrincipal;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "Order Management", description = "APIs for managing orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ApiOperation(value = "Create a new order", notes = "Create a new order for the authenticated user")
    public ResponseEntity<ResponseDataAPI> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            @CurrentUser UserPrincipal userPrincipal) {

        OrderResponse orderResponse = orderService.createOrder(request, userPrincipal.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDataAPI.success(orderResponse, "Order created successfully"));
    }

    @PostMapping("/from-cart")
    @ApiOperation(value = "Create order from cart", notes = "Create a new order from the user's shopping cart")
    public ResponseEntity<ResponseDataAPI> createOrderFromCart(
            @Valid @RequestBody CreateOrderFromCartRequest request,
            @CurrentUser UserPrincipal userPrincipal) {

        OrderResponse orderResponse = orderService.createOrderFromCart(request, userPrincipal.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDataAPI.success(orderResponse, "Order created from cart successfully"));
    }

    @GetMapping("/{orderId}")
    @ApiOperation(value = "Get order by ID", notes = "Get order details by order ID")
    public ResponseEntity<ResponseDataAPI> getOrderById(
            @PathVariable UUID orderId,
            @CurrentUser UserPrincipal userPrincipal) {

        OrderResponse orderResponse = orderService.getOrderById(orderId, userPrincipal.getId());

        return ResponseEntity.ok(
                ResponseDataAPI.success(orderResponse, "Order retrieved successfully"));
    }

    @GetMapping("/number/{orderNumber}")
    @ApiOperation(value = "Get order by number", notes = "Get order details by order number")
    public ResponseEntity<ResponseDataAPI> getOrderByNumber(
            @PathVariable String orderNumber,
            @CurrentUser UserPrincipal userPrincipal) {

        OrderResponse orderResponse = orderService.getOrderByNumber(orderNumber, userPrincipal.getId());

        return ResponseEntity.ok(
                ResponseDataAPI.success(orderResponse, "Order retrieved successfully"));
    }

    @GetMapping
    @ApiOperation(value = "Get user orders", notes = "Get paginated list of user orders")
    public ResponseEntity<ResponseDataAPI> getUserOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @CurrentUser UserPrincipal userPrincipal) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderResponse> orders = orderService.getUserOrders(userPrincipal.getId(), pageable);

        return ResponseEntity.ok(
                ResponseDataAPI.success(orders, "Orders retrieved successfully"));
    }

    @GetMapping("/status/{status}")
    @ApiOperation(value = "Get user orders by status", notes = "Get paginated list of user orders filtered by status")
    public ResponseEntity<ResponseDataAPI> getUserOrdersByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @CurrentUser UserPrincipal userPrincipal) {
        Pageable pageable = PageRequest.of(page, size);

        try {
            var orderStatus = com.pbl.elearning.commerce.domain.enums.OrderStatus.valueOf(status.toUpperCase());
            Page<OrderResponse> orders = orderService.getUserOrdersByStatus(userPrincipal.getId(), orderStatus, pageable);

            return ResponseEntity.ok(
                    ResponseDataAPI.success(orders, "Orders retrieved successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ResponseDataAPI.error("Invalid order status: " + status));
        }
    }

    @GetMapping("/purchased-courses")
    @ApiOperation(value = "Get purchased course IDs", notes = "Get list of course IDs that the user has purchased")
    public ResponseEntity<ResponseDataAPI> getPurchasedCourseIds(
            @CurrentUser UserPrincipal userPrincipal) {

        List<UUID> courseIds = orderService.getPurchasedCourseIds(userPrincipal.getId());

        return ResponseEntity.ok(
                ResponseDataAPI.success(courseIds, "Purchased courses retrieved successfully"));
    }

    @GetMapping("/course/{courseId}/purchased")
    @ApiOperation(value = "Check if course is purchased", notes = "Check if the user has purchased a specific course")
    public ResponseEntity<ResponseDataAPI> hasUserPurchasedCourse(
            @PathVariable UUID courseId,
            @CurrentUser UserPrincipal userPrincipal) {

        boolean hasPurchased = orderService.hasUserPurchasedCourse(userPrincipal.getId(), courseId);

        return ResponseEntity.ok(
                ResponseDataAPI.success(hasPurchased, "Purchase status checked successfully"));
    }

    @DeleteMapping("/{orderId}")
    @ApiOperation(value = "Cancel order", notes = "Cancel a pending order")
    public ResponseEntity<ResponseDataAPI> cancelOrder(
            @PathVariable UUID orderId,
            @CurrentUser UserPrincipal userPrincipal) {
        OrderResponse orderResponse = orderService.cancelOrder(orderId, userPrincipal.getId());

        return ResponseEntity.ok(
                ResponseDataAPI.success(orderResponse, "Order cancelled successfully"));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseDataAPI> handleRuntimeException (RuntimeException ex) {
        log.error("Error in OrderController", ex);
        return ResponseEntity.badRequest()
                .body(ResponseDataAPI.error(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDataAPI> handleException(Exception ex) {
        log.error("Unexpected error in OrderController", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDataAPI.error("An unexpected error occurred"));
    }

}