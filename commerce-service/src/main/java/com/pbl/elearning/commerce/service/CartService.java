package com.pbl.elearning.commerce.service;

import com.pbl.elearning.commerce.domain.Cart;
import com.pbl.elearning.commerce.domain.CartItem;
import com.pbl.elearning.commerce.payload.request.AddToCartRequest;
import com.pbl.elearning.commerce.payload.response.CartResponse;
import com.pbl.elearning.commerce.payload.response.CartSummaryResponse;
import com.pbl.elearning.commerce.repository.CartItemRepository;
import com.pbl.elearning.commerce.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderService orderService;

    @Transactional
    public CartResponse addToCart(AddToCartRequest request, java.util.UUID userId) {
        // 1. Validate if user already purchased this course
        if (orderService.hasUserPurchasedCourse(userId, request.getCourseId())) {
            throw new RuntimeException("You have already purchased this course");
        }

        // Validate if course id is exists in course service
        // CourseResponse course = courseService.getCourseById(request.getCourseId());
        // if (course == null) {
            // throw new RuntimeException("Course not found");
        // }

        // 2. Get or create cart for user
        Cart cart = getOrCreateCart(userId);

        // 3. Check if course already in cart
        CartItem existingItem = cart.getItemByCourseId(request.getCourseId());
        if (existingItem != null) {
            throw new RuntimeException("Course already in cart");
            // log.info("Course {} already in cart for user {}", request.getCourseId(),
            // userId);
        } else {
            // Create new cart item
            CartItem cartItem = createCartItem(request, cart);
            cart.addItem(cartItem);
            log.info("Added course {} to cart for user {}", request.getCourseId(), userId);
        }

        // 4. Save cart
        cart = cartRepository.save(cart);

        return mapToCartResponse(cart);
    }

    // @Transactional
    // public CartResponse updateCartItem(Long courseId, UpdateCartItemRequest
    // request, UUID userId) {
    // Cart cart = getUserCart(userId);

    // CartItem cartItem = cart.getItemByCourseId(courseId);
    // if (cartItem == null) {
    // throw new RuntimeException("Course not found in cart");
    // }

    // cartItem.updateQuantity(request.getQuantity());
    // cart.recalculateAmounts();

    // cart = cartRepository.save(cart);
    // log.info("Updated cart item for course {} for user {}", courseId, userId);

    // return mapToCartResponse(cart);
    // }

    @Transactional
    public CartResponse removeFromCart(UUID courseId, UUID userId) {
        Cart cart = getUserCart(userId);

        cart.removeItemByCourseId(courseId);
        cart = cartRepository.save(cart);

        log.info("Removed course {} from cart for user {}", courseId, userId);
        return mapToCartResponse(cart);
    }

    @Transactional
    public CartResponse clearCart(UUID userId) {
        Cart cart = getUserCart(userId);

        cart.clearItems();
        cart = cartRepository.save(cart);

        log.info("Cleared cart for user {}", userId);
        return mapToCartResponse(cart);
    }

    @Transactional(readOnly = true)
    public CartResponse getCart(UUID userId) {
        Cart cart = cartRepository.findByUserIdWithItems(userId)
                .orElse(createEmptyCart(userId));

        return mapToCartResponse(cart);
    }

    @Transactional(readOnly = true)
    public CartSummaryResponse getCartSummary(UUID userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElse(createEmptyCart(userId));

        return mapToCartSummaryResponse(cart);
    }

    @Transactional(readOnly = true)
    public Integer getCartItemCount(UUID userId) {
        Cart cart = cartRepository.findByUserId(userId).orElse(null);
        return cart != null ? cart.getTotalItems() : 0;
    }

    @Transactional(readOnly = true)
    public List<UUID> getCartCourseIds(UUID userId) {
        return cartItemRepository.findCourseIdsByUserId(userId);
    }

    @Transactional(readOnly = true)
    public boolean isInCart(UUID courseId, UUID userId) {
        return cartItemRepository.findByUserIdAndCourseId(userId, courseId).isPresent();
    }

    @Transactional
    public void cleanupAbandonedCarts() {
        // Clean up carts that haven't been updated in 30 days and are empty
        java.sql.Timestamp cutoffTime = new java.sql.Timestamp(
                System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)); // 30 days ago

        List<Cart> abandonedCarts = cartRepository.findAbandonedCarts(cutoffTime);
        cartRepository.deleteAll(abandonedCarts);

        log.info("Cleaned up {} abandoned carts", abandonedCarts.size());
    }

    // Helper methods
    private Cart getOrCreateCart(UUID userId) {
        return cartRepository.findByUserIdWithItems(userId)
                .orElseGet(() -> createNewCart(userId));
    }

    private Cart getUserCart(UUID userId) {
        return cartRepository.findByUserIdWithItems(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
    }

    private Cart createNewCart(UUID userId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        return cartRepository.save(cart);
    }

    private Cart createEmptyCart(UUID userId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        return cart;
    }

    private CartItem createCartItem(AddToCartRequest request, Cart cart) {
        CartItem cartItem = new CartItem();
        cartItem.setCourseId(request.getCourseId());
        cartItem.setAddedPrice(request.getAddedPrice());
        cartItem.setCart(cart);

        return cartItem;
    }

    private CartResponse mapToCartResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setUserId(cart.getUserId());
        response.setTotalItems(cart.getTotalItems());
        response.setTotalAmount(cart.getTotalAmount());
        response.setDiscountAmount(BigDecimal.ZERO);
        response.setFinalAmount(cart.getTotalAmount());
        response.setCreatedAt(cart.getCreatedAt());
        response.setUpdatedAt(cart.getUpdatedAt());

        // Map items
        List<CartResponse.CartItemResponse> itemResponses = cart.getItems().stream()
                .map(this::mapToCartItemResponse)
                .collect(Collectors.toList());
        response.setItems(itemResponses);

        // Calculate statistics
        response.setIsEmpty(cart.isEmpty());
        response.setHasCoupon(false);
        response.setUniqueCourses(cart.getItems().size());

        response.setTotalSavings(BigDecimal.ZERO);

        return response;
    }

    private CartResponse.CartItemResponse mapToCartItemResponse(CartItem cartItem) {
        CartResponse.CartItemResponse response = new CartResponse.CartItemResponse();
        response.setId(cartItem.getId());
        response.setCourseId(cartItem.getCourseId());
        response.setTotalPrice(cartItem.getAddedPrice());
        response.setDiscountAmount(BigDecimal.ZERO);
        response.setAddedAt(cartItem.getCreatedAt());
        return response;
    }

    private CartSummaryResponse mapToCartSummaryResponse(Cart cart) {
        CartSummaryResponse response = new CartSummaryResponse();
        response.setCartId(cart.getId());
        response.setUserId(cart.getUserId());
        response.setTotalItems(cart.getTotalItems());
        response.setTotalAmount(cart.getTotalAmount());
        response.setDiscountAmount(BigDecimal.ZERO);
        response.setFinalAmount(cart.getTotalAmount());
        response.setIsEmpty(cart.isEmpty());
        response.setHasCoupon(false);
        response.setUniqueCourses(cart.getItems().size());

        if (!cart.isEmpty()) {
            BigDecimal avgPrice = cart.getTotalAmount()
                    .divide(BigDecimal.valueOf(cart.getItems().size()), 2, RoundingMode.HALF_UP);
            response.setAverageItemPrice(avgPrice);

            response.setTotalSavings(BigDecimal.ZERO);
        } else {
            response.setAverageItemPrice(BigDecimal.ZERO);
            response.setTotalSavings(BigDecimal.ZERO);
        }

        return response;
    }
}
