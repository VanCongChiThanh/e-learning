package com.pbl.elearning.web.endpoint.commerce;

import com.pbl.elearning.commerce.payload.request.AddToCartRequest;
import com.pbl.elearning.commerce.payload.response.CartResponse;
import com.pbl.elearning.commerce.payload.response.CartSummaryResponse;
import com.pbl.elearning.commerce.service.CartService;
import com.pbl.elearning.common.payload.general.ResponseDataAPI;
import com.pbl.elearning.security.domain.UserPrincipal;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "Shopping Cart", description = "APIs for managing shopping cart")
public class CartController {

        private final CartService cartService;

    @PostMapping("/add")
    @PreAuthorize("hasAnyRole('LEARNER', 'ADMIN')")
    @ApiOperation(value = "Add item to cart", notes = "Add a course to the user's shopping cart")
    public ResponseEntity<ResponseDataAPI> addToCart(
            @Valid @RequestBody AddToCartRequest request,
            Authentication authentication) {

            UUID userId = getUserIdFromAuthentication(authentication);
        CartResponse cartResponse = cartService.addToCart(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDataAPI.success(cartResponse, "Item added to cart successfully"));
    }

        @GetMapping
        @ApiOperation(value = "Get cart", notes = "Get the user's current shopping cart")
        public ResponseEntity<ResponseDataAPI> getCart(Authentication authentication) {

                UUID userId = getUserIdFromAuthentication(authentication);
                CartResponse cartResponse = cartService.getCart(userId);

                return ResponseEntity.ok(
                                ResponseDataAPI.success(cartResponse, "Cart retrieved successfully"));
        }

        @GetMapping("/summary")
        @ApiOperation(value = "Get cart summary", notes = "Get a summary of the user's shopping cart")
        public ResponseEntity<ResponseDataAPI> getCartSummary(
                        Authentication authentication) {

                UUID userId = getUserIdFromAuthentication(authentication);
                CartSummaryResponse summaryResponse = cartService.getCartSummary(userId);

                return ResponseEntity.ok(
                                ResponseDataAPI.success(summaryResponse, "Cart summary retrieved successfully"));
        }

        // @PutMapping("/items/{courseId}")
        // @ApiOperation(value = "Update cart item", notes = "Update the quantity of an item in the cart")
        // public ResponseEntity<ResponseDataAPI> updateCartItem(
        //                 @PathVariable Long courseId,
        //                 @Valid @RequestBody UpdateCartItemRequest request
        // , Authentication authentication
        // ) {

        //         UUID userId = getUserIdFromAuthentication(authentication);
        //         CartResponse cartResponse = cartService.updateCartItem(courseId, request, userId);

        //         return ResponseEntity.ok(
        //                         ResponseDataAPI.success(cartResponse, "Cart item updated successfully"));
        // }

        @DeleteMapping("/items/{courseId}")
        @ApiOperation(value = "Remove item from cart", notes = "Remove a specific course from the cart")
        public ResponseEntity<ResponseDataAPI> removeFromCart(
                        @PathVariable UUID courseId,
                        Authentication authentication) {

                UUID userId = getUserIdFromAuthentication(authentication);
                CartResponse cartResponse = cartService.removeFromCart(courseId, userId);

                return ResponseEntity.ok(
                                ResponseDataAPI.success(cartResponse, "Item removed from cart successfully"));
        }

        @DeleteMapping("/clear")
        @ApiOperation(value = "Clear cart", notes = "Remove all items from the cart")
        public ResponseEntity<ResponseDataAPI> clearCart(Authentication authentication) {

                UUID userId = getUserIdFromAuthentication(authentication);
                CartResponse cartResponse = cartService.clearCart(userId);

                return ResponseEntity.ok(
                                ResponseDataAPI.success(cartResponse, "Cart cleared successfully"));
        }

        @GetMapping("/count")
        @ApiOperation(value = "Get cart item count", notes = "Get the number of items in the cart")
        public ResponseEntity<ResponseDataAPI> getCartItemCount(Authentication authentication) {

                UUID userId = getUserIdFromAuthentication(authentication);
                Integer itemCount = cartService.getCartItemCount(userId);

                return ResponseEntity.ok(
                                ResponseDataAPI.success(itemCount, "Cart item count retrieved successfully"));
        }

        @GetMapping("/courses")
        @ApiOperation(value = "Get cart course IDs", notes = "Get list of course IDs in the cart")
        public ResponseEntity<ResponseDataAPI> getCartCourseIds(Authentication authentication) {

                UUID userId = getUserIdFromAuthentication(authentication);
                List<UUID> courseIds = cartService.getCartCourseIds(userId);

                return ResponseEntity.ok(
                                ResponseDataAPI.success(courseIds, "Cart course IDs retrieved successfully"));
        }

        @GetMapping("/check/{courseId}")
        @ApiOperation(value = "Check if course is in cart", notes = "Check if a specific course is in the cart")
        public ResponseEntity<ResponseDataAPI> isInCart(
                        @PathVariable UUID courseId,
                        Authentication authentication) {

                UUID userId = getUserIdFromAuthentication(authentication);
                Boolean isInCart = cartService.isInCart(courseId, userId);

                return ResponseEntity.ok(
                                ResponseDataAPI.success(isInCart, "Cart check completed successfully"));
        }

        @ExceptionHandler(RuntimeException.class)
        public ResponseEntity<ResponseDataAPI> handleRuntimeException(RuntimeException ex) {
                log.error("Error in CartController", ex);
                return ResponseEntity.badRequest()
                                .body(ResponseDataAPI.error(ex.getMessage()));
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ResponseDataAPI> handleException(Exception ex) {
                log.error("Unexpected error in CartController", ex);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ResponseDataAPI.error("An unexpected error occurred"));
        }

        private UUID getUserIdFromAuthentication(Authentication authentication) {

                if (authentication != null && authentication.getPrincipal() != null) {
                        // Example implementation - adjust based on your User details implementation
                        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                        return userPrincipal.getId();
                }

                throw new RuntimeException("User not authenticated");
        }
}
