package com.pbl.elearning.commerce.service;

import com.pbl.elearning.commerce.domain.Cart;
import com.pbl.elearning.commerce.domain.CartItem;
import com.pbl.elearning.commerce.payload.request.AddToCartRequest;
import com.pbl.elearning.commerce.payload.request.ApplyCouponRequest;
import com.pbl.elearning.commerce.payload.request.UpdateCartItemRequest;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderService orderService;

    @Transactional
    public CartResponse addToCart(AddToCartRequest request, Long userId) {
        // 1. Validate if user already purchased this course
        if (orderService.hasUserPurchasedCourse(userId, request.getCourseId())) {
            throw new RuntimeException("You have already purchased this course");
        }

        // 2. Get or create cart for user
        Cart cart = getOrCreateCart(userId);

        // 3. Check if course already in cart
        CartItem existingItem = cart.getItemByCourseId(request.getCourseId());
        if (existingItem != null) {
            // Update quantity
            existingItem.updateQuantity(existingItem.getQuantity() + request.getQuantity());
            log.info("Updated quantity for course {} in cart for user {}", request.getCourseId(), userId);
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

    @Transactional
    public CartResponse updateCartItem(Long courseId, UpdateCartItemRequest request, Long userId) {
        Cart cart = getUserCart(userId);

        CartItem cartItem = cart.getItemByCourseId(courseId);
        if (cartItem == null) {
            throw new RuntimeException("Course not found in cart");
        }

        cartItem.updateQuantity(request.getQuantity());
        cart.recalculateAmounts();

        cart = cartRepository.save(cart);
        log.info("Updated cart item for course {} for user {}", courseId, userId);

        return mapToCartResponse(cart);
    }

    @Transactional
    public CartResponse removeFromCart(Long courseId, Long userId) {
        Cart cart = getUserCart(userId);

        cart.removeItemByCourseId(courseId);
        cart = cartRepository.save(cart);

        log.info("Removed course {} from cart for user {}", courseId, userId);
        return mapToCartResponse(cart);
    }

    @Transactional
    public CartResponse clearCart(Long userId) {
        Cart cart = getUserCart(userId);

        cart.clearItems();
        cart = cartRepository.save(cart);

        log.info("Cleared cart for user {}", userId);
        return mapToCartResponse(cart);
    }

    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        Cart cart = cartRepository.findByUserIdWithItems(userId)
                .orElse(createEmptyCart(userId));

        return mapToCartResponse(cart);
    }

    @Transactional(readOnly = true)
    public CartSummaryResponse getCartSummary(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElse(createEmptyCart(userId));

        return mapToCartSummaryResponse(cart);
    }

    @Transactional
    public CartResponse applyCoupon(ApplyCouponRequest request, Long userId) {
        Cart cart = getUserCart(userId);

        if (cart.isEmpty()) {
            throw new RuntimeException("Cannot apply coupon to empty cart");
        }

        // TODO: Integrate with coupon service to validate coupon
        // For now, using simple demo coupons
        applyDiscountCoupon(cart, request.getCouponCode());

        cart = cartRepository.save(cart);
        log.info("Applied coupon {} to cart for user {}", request.getCouponCode(), userId);

        return mapToCartResponse(cart);
    }

    @Transactional
    public CartResponse removeCoupon(Long userId) {
        Cart cart = getUserCart(userId);

        cart.removeCoupon();
        cart = cartRepository.save(cart);

        log.info("Removed coupon from cart for user {}", userId);
        return mapToCartResponse(cart);
    }

    @Transactional(readOnly = true)
    public Integer getCartItemCount(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElse(null);
        return cart != null ? cart.getTotalItems() : 0;
    }

    @Transactional(readOnly = true)
    public List<Long> getCartCourseIds(Long userId) {
        return cartItemRepository.findCourseIdsByUserId(userId);
    }

    @Transactional(readOnly = true)
    public boolean isInCart(Long courseId, Long userId) {
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
    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserIdWithItems(userId)
                .orElseGet(() -> createNewCart(userId));
    }

    private Cart getUserCart(Long userId) {
        return cartRepository.findByUserIdWithItems(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
    }

    private Cart createNewCart(Long userId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        return cartRepository.save(cart);
    }

    private Cart createEmptyCart(Long userId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        return cart;
    }

    private CartItem createCartItem(AddToCartRequest request, Cart cart) {
        CartItem cartItem = new CartItem();
        cartItem.setCourseId(request.getCourseId());
        cartItem.setCourseName(request.getCourseName());
        cartItem.setCoursePrice(request.getCoursePrice());
        cartItem.setQuantity(request.getQuantity());
        cartItem.setCourseDescription(request.getCourseDescription());
        cartItem.setCourseThumbnail(request.getCourseThumbnail());
        cartItem.setInstructorName(request.getInstructorName());
        cartItem.setInstructorId(request.getInstructorId());
        cartItem.setCourseDurationMinutes(request.getCourseDurationMinutes());
        cartItem.setCourseLevel(request.getCourseLevel());
        cartItem.setCourseCategory(request.getCourseCategory());
        cartItem.setOriginalPrice(request.getOriginalPrice());
        cartItem.setDiscountAmount(request.getDiscountAmount());
        cartItem.setDiscountPercentage(request.getDiscountPercentage());
        cartItem.setCart(cart);

        cartItem.calculateTotalPrice();
        return cartItem;
    }

    private void applyDiscountCoupon(Cart cart, String couponCode) {
        // Simple demo implementation - replace with actual coupon service integration
        switch (couponCode.toUpperCase()) {
            case "DISCOUNT10":
                BigDecimal discount10 = cart.getTotalAmount().multiply(BigDecimal.valueOf(0.1));
                cart.applyCoupon(couponCode, 10, discount10);
                break;
            case "DISCOUNT20":
                BigDecimal discount20 = cart.getTotalAmount().multiply(BigDecimal.valueOf(0.2));
                cart.applyCoupon(couponCode, 20, discount20);
                break;
            case "DISCOUNT50":
                BigDecimal discount50 = cart.getTotalAmount().multiply(BigDecimal.valueOf(0.5));
                cart.applyCoupon(couponCode, 50, discount50);
                break;
            case "FREESHIP":
                // For demonstration - could be shipping discount
                cart.applyCoupon(couponCode, 0, BigDecimal.valueOf(50000));
                break;
            default:
                throw new RuntimeException("Invalid coupon code: " + couponCode);
        }
    }

    private CartResponse mapToCartResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setUserId(cart.getUserId());
        response.setTotalItems(cart.getTotalItems());
        response.setTotalAmount(cart.getTotalAmount());
        response.setDiscountAmount(cart.getDiscountAmount());
        response.setFinalAmount(cart.getFinalAmount());
        response.setCouponCode(cart.getCouponCode());
        response.setDiscountPercentage(cart.getDiscountPercentage());
        response.setCreatedAt(cart.getCreatedAt());
        response.setUpdatedAt(cart.getUpdatedAt());

        // Map items
        List<CartResponse.CartItemResponse> itemResponses = cart.getItems().stream()
                .map(this::mapToCartItemResponse)
                .collect(Collectors.toList());
        response.setItems(itemResponses);

        // Calculate statistics
        response.setIsEmpty(cart.isEmpty());
        response.setHasCoupon(cart.getCouponCode() != null);
        response.setUniqueCourses(cart.getItems().size());

        BigDecimal totalSavings = cart.getItems().stream()
                .map(CartItem::getSavings)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(cart.getDiscountAmount());
        response.setTotalSavings(totalSavings);

        return response;
    }

    private CartResponse.CartItemResponse mapToCartItemResponse(CartItem cartItem) {
        CartResponse.CartItemResponse response = new CartResponse.CartItemResponse();
        response.setId(cartItem.getId());
        response.setCourseId(cartItem.getCourseId());
        response.setCourseName(cartItem.getCourseName());
        response.setCoursePrice(cartItem.getCoursePrice());
        response.setQuantity(cartItem.getQuantity());
        response.setTotalPrice(cartItem.getTotalPrice());
        response.setCourseDescription(cartItem.getCourseDescription());
        response.setCourseThumbnail(cartItem.getCourseThumbnail());
        response.setInstructorName(cartItem.getInstructorName());
        response.setInstructorId(cartItem.getInstructorId());
        response.setCourseDurationMinutes(cartItem.getCourseDurationMinutes());
        response.setCourseLevel(cartItem.getCourseLevel());
        response.setCourseCategory(cartItem.getCourseCategory());
        response.setOriginalPrice(cartItem.getOriginalPrice());
        response.setDiscountAmount(cartItem.getDiscountAmount());
        response.setDiscountPercentage(cartItem.getDiscountPercentage());
        response.setSavings(cartItem.getSavings());
        response.setHasDiscount(cartItem.hasDiscount());
        response.setAddedAt(cartItem.getCreatedAt());
        return response;
    }

    private CartSummaryResponse mapToCartSummaryResponse(Cart cart) {
        CartSummaryResponse response = new CartSummaryResponse();
        response.setCartId(cart.getId());
        response.setUserId(cart.getUserId());
        response.setTotalItems(cart.getTotalItems());
        response.setTotalAmount(cart.getTotalAmount());
        response.setDiscountAmount(cart.getDiscountAmount());
        response.setFinalAmount(cart.getFinalAmount());
        response.setCouponCode(cart.getCouponCode());
        response.setIsEmpty(cart.isEmpty());
        response.setHasCoupon(cart.getCouponCode() != null);
        response.setUniqueCourses(cart.getItems().size());

        if (!cart.isEmpty()) {
            BigDecimal avgPrice = cart.getTotalAmount()
                    .divide(BigDecimal.valueOf(cart.getItems().size()), 2, RoundingMode.HALF_UP);
            response.setAverageItemPrice(avgPrice);

            BigDecimal totalSavings = cart.getItems().stream()
                    .map(CartItem::getSavings)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .add(cart.getDiscountAmount());
            response.setTotalSavings(totalSavings);
        } else {
            response.setAverageItemPrice(BigDecimal.ZERO);
            response.setTotalSavings(BigDecimal.ZERO);
        }

        return response;
    }
}
