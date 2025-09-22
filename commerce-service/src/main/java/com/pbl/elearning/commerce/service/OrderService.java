package com.pbl.elearning.commerce.service;

import com.pbl.elearning.commerce.domain.*;
import com.pbl.elearning.commerce.domain.enums.OrderStatus;
import com.pbl.elearning.commerce.payload.request.CreateOrderFromCartRequest;
import com.pbl.elearning.commerce.payload.request.CreateOrderRequest;
import com.pbl.elearning.commerce.payload.response.OrderResponse;
import com.pbl.elearning.commerce.repository.CartRepository;
import com.pbl.elearning.commerce.repository.OrderItemRepository;
import com.pbl.elearning.commerce.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request, Long userId) {
        // 1. Validate request
        validateOrderRequest(request, userId);

        // 2. Create order entity
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setUserId(userId);
        order.setStatus(OrderStatus.PENDING);
        order.setNotes(request.getNotes());
        order.setCouponCode(request.getCouponCode());

        // 3. Create order items
        List<OrderItem> orderItems = request.getItems().stream()
                .map(itemRequest -> createOrderItem(itemRequest, order))
                .collect(Collectors.toList());

        order.getItems().addAll(orderItems);

        // 4. Calculate amounts
        order.calculateTotalAmount();

        // Apply discount if coupon code is provided
        if (request.getCouponCode() != null && !request.getCouponCode().trim().isEmpty()) {
            applyDiscount(order, request.getCouponCode());
        }

        // 5. Save order
        Order savedOrder = orderRepository.save(order);

        log.info("Created order {} for user {}", savedOrder.getOrderNumber(), userId);
        return mapToOrderResponse(savedOrder);
    }

    @Transactional
    public OrderResponse createOrderFromCart(CreateOrderFromCartRequest request, Long userId) {
        // 1. Get user's cart
        Cart cart = cartRepository.findByUserIdWithItems(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.isEmpty()) {
            throw new RuntimeException("Cannot create order from empty cart");
        }

        // 2. Validate cart items (check if courses still available and user hasn't
        // purchased them)
        for (CartItem cartItem : cart.getItems()) {
            if (hasUserPurchasedCourse(userId, cartItem.getCourseId())) {
                throw new RuntimeException("You have already purchased course: " + cartItem.getCourseName());
            }
        }

        // 3. Create order entity
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setUserId(userId);
        order.setStatus(OrderStatus.PENDING);
        order.setNotes(request.getNotes());
        order.setCouponCode(cart.getCouponCode());
        order.setDiscountPercentage(cart.getDiscountPercentage());

        // 4. Convert cart items to order items
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> convertCartItemToOrderItem(cartItem, order))
                .collect(Collectors.toList());

        order.getItems().addAll(orderItems);

        // 5. Set amounts from cart
        order.setTotalAmount(cart.getTotalAmount());
        order.setDiscountAmount(cart.getDiscountAmount());
        order.setFinalAmount(cart.getFinalAmount());

        // 6. Save order
        Order savedOrder = orderRepository.save(order);

        // 7. Clear cart if requested
        if (request.getClearCartAfterOrder()) {
            cart.clearItems();
            cartRepository.save(cart);
            log.info("Cart cleared for user {} after creating order {}", userId, savedOrder.getOrderNumber());
        }

        log.info("Created order {} from cart for user {}", savedOrder.getOrderNumber(), userId);
        return mapToOrderResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied to this order");
        }

        return mapToOrderResponse(order);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderByNumber(String orderNumber, Long userId) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied to this order");
        }

        return mapToOrderResponse(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getUserOrders(Long userId, Pageable pageable) {
        Page<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return orders.map(this::mapToOrderResponse);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getUserOrdersByStatus(Long userId, OrderStatus status, Pageable pageable) {
        Page<Order> orders = orderRepository.findByUserIdAndStatus(userId, status, pageable);
        return orders.map(this::mapToOrderResponse);
    }

    @Transactional(readOnly = true)
    public List<Long> getPurchasedCourseIds(Long userId) {
        return orderItemRepository.findPurchasedCourseIdsByUserId(userId);
    }

    @Transactional(readOnly = true)
    public boolean hasUserPurchasedCourse(Long userId, Long courseId) {
        List<OrderStatus> paidStatuses = List.of(OrderStatus.PAID, OrderStatus.DELIVERED);
        return orderRepository.findByUserIdAndCourseIdAndStatusIn(userId, courseId, paidStatuses)
                .isPresent();
    }

    @Transactional
    public void grantCourseAccess(Order order) {
        try {
            // Update order status to delivered
            order.setStatus(OrderStatus.DELIVERED);
            order.setDeliveredAt(new Timestamp(System.currentTimeMillis()));
            orderRepository.save(order);

            // Log course access granted
            for (OrderItem item : order.getItems()) {
                log.info("Granted access to course {} for user {} via order {}",
                        item.getCourseId(), order.getUserId(), order.getOrderNumber());
            }

            // TODO: Integrate with enrollment service to actually grant course access
            // enrollmentService.grantCourseAccess(order.getUserId(), order.getItems());

            // TODO: Send notification email
            // emailService.sendPurchaseConfirmation(order);

        } catch (Exception e) {
            log.error("Failed to grant course access for order {}", order.getOrderNumber(), e);
        }
    }

    @Transactional
    public OrderResponse cancelOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("Access denied to this order");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Order cannot be cancelled");
        }

        order.setStatus(OrderStatus.CANCELLED);
        order = orderRepository.save(order);

        log.info("Cancelled order {} for user {}", order.getOrderNumber(), userId);
        return mapToOrderResponse(order);
    }

    private void validateOrderRequest(CreateOrderRequest request, Long userId) {
        // Validate user exists (this should be done by the calling service)

        // Validate course availability and prices
        for (CreateOrderRequest.OrderItemRequest item : request.getItems()) {
            // TODO: Validate course exists and is available for purchase
            // Course course = courseService.getCourseById(item.getCourseId());
            // if (!course.isAvailableForPurchase()) {
            // throw new RuntimeException("Course " + item.getCourseId() + " is not
            // available for purchase");
            // }

            // Validate user hasn't already purchased this course
            if (hasUserPurchasedCourse(userId, item.getCourseId())) {
                throw new RuntimeException("User has already purchased course: " + item.getCourseId());
            }
        }
    }

    private OrderItem createOrderItem(CreateOrderRequest.OrderItemRequest itemRequest, Order order) {
        OrderItem orderItem = new OrderItem();
        orderItem.setCourseId(itemRequest.getCourseId());
        orderItem.setCourseName(itemRequest.getCourseName());
        orderItem.setCoursePrice(BigDecimal.valueOf(itemRequest.getCoursePrice()));
        orderItem.setQuantity(itemRequest.getQuantity());
        orderItem.setCourseDescription(itemRequest.getCourseDescription());
        orderItem.setCourseThumbnail(itemRequest.getCourseThumbnail());
        orderItem.setInstructorName(itemRequest.getInstructorName());
        orderItem.setOrder(order);

        // Calculate total price
        orderItem.calculateTotalPrice();

        return orderItem;
    }

    private OrderItem convertCartItemToOrderItem(CartItem cartItem, Order order) {
        OrderItem orderItem = new OrderItem();
        orderItem.setCourseId(cartItem.getCourseId());
        orderItem.setCourseName(cartItem.getCourseName());
        orderItem.setCoursePrice(cartItem.getCoursePrice());
        orderItem.setQuantity(cartItem.getQuantity());
        orderItem.setTotalPrice(cartItem.getTotalPrice());
        orderItem.setDiscountAmount(cartItem.getDiscountAmount());
        orderItem.setCourseDescription(cartItem.getCourseDescription());
        orderItem.setCourseThumbnail(cartItem.getCourseThumbnail());
        orderItem.setInstructorName(cartItem.getInstructorName());
        orderItem.setOrder(order);

        return orderItem;
    }

    private void applyDiscount(Order order, String couponCode) {
        // TODO: Implement coupon validation and discount calculation
        // For now, applying a simple 10% discount for demo purposes
        if ("DISCOUNT10".equals(couponCode)) {
            BigDecimal discount = order.getTotalAmount().multiply(BigDecimal.valueOf(0.1));
            order.setDiscountAmount(discount);
            order.setDiscountPercentage(10);
            order.setFinalAmount(order.getTotalAmount().subtract(discount));
        } else if ("DISCOUNT20".equals(couponCode)) {
            BigDecimal discount = order.getTotalAmount().multiply(BigDecimal.valueOf(0.2));
            order.setDiscountAmount(discount);
            order.setDiscountPercentage(20);
            order.setFinalAmount(order.getTotalAmount().subtract(discount));
        } else {
            // Invalid coupon code
            log.warn("Invalid coupon code used: {}", couponCode);
            order.setFinalAmount(order.getTotalAmount());
        }
    }

    private String generateOrderNumber() {
        // Generate unique order number with format: ORDER-YYYYMMDD-HHMMSS-XXX
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        int random = (int) (Math.random() * 1000);
        return String.format("ORDER-%s-%03d", timestamp, random);
    }

    private OrderResponse mapToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderNumber(order.getOrderNumber());
        response.setUserId(order.getUserId());
        response.setTotalAmount(order.getTotalAmount());
        response.setDiscountAmount(order.getDiscountAmount());
        response.setFinalAmount(order.getFinalAmount());
        response.setStatus(order.getStatus());
        response.setNotes(order.getNotes());
        response.setCouponCode(order.getCouponCode());
        response.setDiscountPercentage(order.getDiscountPercentage());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        response.setDeliveredAt(order.getDeliveredAt());

        // Map order items
        List<OrderResponse.OrderItemResponse> itemResponses = order.getItems().stream()
                .map(this::mapToOrderItemResponse)
                .collect(Collectors.toList());
        response.setItems(itemResponses);

        // Map payment summary
        if (order.getPayment() != null) {
            OrderResponse.PaymentSummaryResponse paymentSummary = new OrderResponse.PaymentSummaryResponse();
            paymentSummary.setId(order.getPayment().getId());
            paymentSummary.setOrderCode(order.getPayment().getOrderCode());
            paymentSummary.setPaymentMethod(order.getPayment().getPaymentMethod().name());
            paymentSummary.setStatus(order.getPayment().getStatus().name());
            paymentSummary.setCheckoutUrl(order.getPayment().getCheckoutUrl());
            paymentSummary.setPaidAt(order.getPayment().getPaidAt());
            paymentSummary.setExpiresAt(order.getPayment().getExpiresAt());
            response.setPayment(paymentSummary);
        }

        return response;
    }

    private OrderResponse.OrderItemResponse mapToOrderItemResponse(OrderItem orderItem) {
        OrderResponse.OrderItemResponse response = new OrderResponse.OrderItemResponse();
        response.setId(orderItem.getId());
        response.setCourseId(orderItem.getCourseId());
        response.setCourseName(orderItem.getCourseName());
        response.setCoursePrice(orderItem.getCoursePrice());
        response.setQuantity(orderItem.getQuantity());
        response.setTotalPrice(orderItem.getTotalPrice());
        response.setDiscountAmount(orderItem.getDiscountAmount());
        response.setCourseDescription(orderItem.getCourseDescription());
        response.setCourseThumbnail(orderItem.getCourseThumbnail());
        response.setInstructorName(orderItem.getInstructorName());
        return response;
    }
}
