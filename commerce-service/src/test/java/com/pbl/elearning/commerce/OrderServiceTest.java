package com.pbl.elearning.commerce;

import com.pbl.elearning.commerce.domain.Cart;
import com.pbl.elearning.commerce.domain.CartItem;
import com.pbl.elearning.commerce.domain.Order;
import com.pbl.elearning.commerce.domain.OrderItem;
import com.pbl.elearning.commerce.domain.enums.OrderStatus;
import com.pbl.elearning.commerce.payload.request.CreateOrderFromCartRequest;
import com.pbl.elearning.commerce.payload.request.CreateOrderRequest;
import com.pbl.elearning.commerce.payload.response.OrderResponse;
import com.pbl.elearning.commerce.repository.CartRepository;
import com.pbl.elearning.commerce.repository.OrderItemRepository;
import com.pbl.elearning.commerce.repository.OrderRepository;
import com.pbl.elearning.commerce.service.CourseClient;
import com.pbl.elearning.commerce.service.EnrollmentClient;
import com.pbl.elearning.commerce.service.OrderService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    // Mock các object không thuộc phạm vi test
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private EnrollmentClient enrollmentClient;
    @Mock
    private CourseClient courseClient;
    @Mock
    private Cart mockedCart;
    @InjectMocks
    private OrderService orderService;

    private UUID userId;
    private UUID courseId1;
    private UUID courseId2;
    private CreateOrderRequest createOrderRequest;

    private CartItem cartItem1;
    private CartItem cartItem2;
    private CreateOrderFromCartRequest createFromCartRequest;

    private ArgumentCaptor<Order> orderCaptor;

    @BeforeEach
    void setUp() {
        // 1. Thiết lập dữ liệu cho createOrder tests
        userId = UUID.randomUUID();
        courseId1 = UUID.randomUUID();
        courseId2 = UUID.randomUUID();

        // 1.1 Tạo 2 items giả lập trong order request
        CreateOrderRequest.OrderItemRequest item1 = new CreateOrderRequest.OrderItemRequest();
        item1.setCourseId(courseId1);
        item1.setCoursePrice(100.0);

        CreateOrderRequest.OrderItemRequest item2 = new CreateOrderRequest.OrderItemRequest();
        item2.setCourseId(courseId2);
        item2.setCoursePrice(50.0);

        // 1.2 Khởi tạo request mẫu
        createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setNotes("Create order");
        createOrderRequest.setItems(List.of(item1, item2));

        // ----

        // 2. Thiết lập CartItems cho createOrderFromCart tests
        // 2.1 Tạo 2 cartItem giả lập trong cart
        cartItem1 = new CartItem();
        cartItem1.setCourseId(courseId1);
        cartItem1.setAddedPrice(new BigDecimal("100.0"));
        cartItem1.setId(UUID.randomUUID());

        cartItem2 = new CartItem();
        cartItem2.setCourseId(courseId2);
        cartItem2.setAddedPrice(new BigDecimal("50.0"));
        cartItem2.setId(UUID.randomUUID());

        // 2.2 Khởi tạo request mẫu
        createFromCartRequest = new CreateOrderFromCartRequest();
        createFromCartRequest.setNotes("Creating order from cart");

        orderCaptor = ArgumentCaptor.forClass(Order.class);
    }

    /**
     * Create Order Tests
     */

    @Test
    void createOrder_ShouldReturnOrderResponse_WhenValidRequest() {
        // --- Arrange ---

        // 1. Giả lập validate
        // 1.1 Giả lập Khóa học tồn tại
        when(courseClient.isCourseExist(courseId1.toString())).thenReturn(true);
        when(courseClient.isCourseExist(courseId2.toString())).thenReturn(true);

        // 1.2. Giả lập user chưa mua khóa học nào
        when(orderRepository.findByUserIdAndCourseIdAndStatusIn(eq(userId), eq(courseId1), anyList()))
                .thenReturn(Optional.empty());
        when(orderRepository.findByUserIdAndCourseIdAndStatusIn(eq(userId), eq(courseId2), anyList()))
                .thenReturn(Optional.empty());

        // 2. Giả lập việc lưu order (orderRepository.save)
        when(orderRepository.save(orderCaptor.capture())).thenAnswer(invocation -> {
            // lấy ra order được truyền vào
            Order orderToSave = invocation.getArgument(0);
            orderToSave.setId(UUID.randomUUID()); 

            BigDecimal total = orderToSave.getItems().stream()
                    .map(OrderItem::getUnitPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            orderToSave.setTotalAmount(total);
            return orderToSave;
        });

        // 3. Giả lập việc map response (mapToOrderItemResponse)
        CourseClient.CourseResponse courseResponse1 = new CourseClient.CourseResponse();
        courseResponse1.setData(Map.of("title", "Course 1 Title", "image", "img1.jpg"));
        when(courseClient.getCourseDetails(courseId1.toString())).thenReturn(courseResponse1);

        CourseClient.CourseResponse courseResponse2 = new CourseClient.CourseResponse();
        courseResponse2.setData(Map.of("title", "Course 2 Title", "image", "img2.jpg"));
        when(courseClient.getCourseDetails(courseId2.toString())).thenReturn(courseResponse2);


        // --- Act  ---
        OrderResponse response = orderService.createOrder(createOrderRequest, userId);


        // --- Assert (Xác minh) ---

        // 1.  Xác minh `orderRepository.save` được gọi 1 lần
        verify(orderRepository, times(1)).save(any(Order.class));

        Order savedOrder = orderCaptor.getValue();

        // 2. Kiểm tra các thuộc tính của Order được lưu
        assertNotNull(savedOrder);
        assertEquals(userId, savedOrder.getUserId());
        assertEquals(OrderStatus.PENDING, savedOrder.getStatus());
        assertEquals("Create order", savedOrder.getNotes());
        assertTrue(savedOrder.getOrderNumber().startsWith("ORDER-"));
        assertEquals(2, savedOrder.getItems().size());

        // 3. Kiểm tra tổng số tiền (100 + 50 = 150)
        assertEquals(0, new BigDecimal("150.0").compareTo(savedOrder.getTotalAmount()));

        // 4. Kiểm tra OrderItem
        OrderItem item1 = savedOrder.getItems().stream()
                .filter(i -> i.getCourseId().equals(courseId1)).findFirst().orElse(null);
        assertNotNull(item1);
        assertEquals(0, new BigDecimal("100.0").compareTo(item1.getUnitPrice()));
        assertEquals(savedOrder, item1.getOrder()); // Kiểm tra quan hệ

        // 5. Kiểm tra OrderResponse
        assertNotNull(response);
        assertEquals(savedOrder.getId(), response.getId());
        assertEquals(userId, response.getUserId());
        assertEquals(0, new BigDecimal("150.0").compareTo(response.getFinalAmount()));
        assertEquals(2, response.getItems().size());
        assertEquals("Course 1 Title", response.getItems().get(0).getCourseTitle());
        assertEquals("Course 2 Title", response.getItems().get(1).getCourseTitle());
    }

    @Test
    void createOrder_ShouldThrowException_WhenCourseDoesNotExist() {
        // --- Arrange ---
        // 1. Giả lập khóa học 1 không tồn tại
        when(courseClient.isCourseExist(courseId1.toString())).thenReturn(false);

        // --- Act & Assert ---
        // 1. Kiểm tra xem một RuntimeException có được ném ra không
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(createOrderRequest, userId);
        });

        // 2. Kiểm tra nội dung của exception
        assertTrue(exception.getMessage().contains("Can not found course with ID:"));

        // 3. Kiểm tra `orderRepository.save` không được gọi
        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrder_ShouldThrowException_WhenUserAlreadyPurchasedCourse() {
        // --- Arrange ---
        // 1. Giả lập course 1 tồn tại
        when(courseClient.isCourseExist(courseId1.toString())).thenReturn(true);

        // 2. Giả lập user ĐÃ MUA course 1
        when(orderRepository.findByUserIdAndCourseIdAndStatusIn(eq(userId), eq(courseId1), anyList()))
                .thenReturn(Optional.of(new Order())); 

        // --- Act & Assert ---
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(createOrderRequest, userId);
        });

        // 1. Kiểm tra nội dung của exception
        assertTrue(exception.getMessage().contains("User has already purchased course:"));

        // 2. Kiểm tra rằng `orderRepository.save` không được gọi
        verify(orderRepository, never()).save(any());
    }

    @Test
    void testCreateOrder_ShouldThrowException_WithEmptyItemsList() {
        // --- Arrange ---
        // 1. Tạo request với danh sách items rỗng
        CreateOrderRequest emptyRequest = new CreateOrderRequest();
        emptyRequest.setNotes("Empty order test");
        emptyRequest.setItems(Collections.emptyList()); // Danh sách rỗng

        // --- Act & Assert ---
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(emptyRequest, userId);
        });

        // 1. Xác minh `save` không được gọi
        verify(orderRepository, times(1)).save(any(Order.class));

        // 2.Kiểm tra nội dung exception
        assertTrue(exception.getMessage().contains("Order items cannot be empty"));
    }

    /**
     * Create Order from Cart Tests
     */

    private void setupMockSuccessCart() {
        // 1. Giả lập tìm thấy cart và cart không rỗng
        when(cartRepository.findByUserIdWithItems(userId)).thenReturn(Optional.of(mockedCart));
        when(mockedCart.isEmpty()).thenReturn(false);

        // 2. Giả lập cart có 2 item
        when(mockedCart.getItems()).thenReturn(List.of(cartItem1, cartItem2));

        // 3. Giả lập tổng tiền của cart
        when(mockedCart.getTotalAmount()).thenReturn(new BigDecimal("150.0"));

        // 4. Giả lập validation (user chưa mua khóa học nào)
        when(orderRepository.findByUserIdAndCourseIdAndStatusIn(eq(userId), eq(courseId1), anyList()))
                .thenReturn(Optional.empty());
        when(orderRepository.findByUserIdAndCourseIdAndStatusIn(eq(userId), eq(courseId2), anyList()))
                .thenReturn(Optional.empty());

        // 5. Giả lập việc lưu Order 
        when(orderRepository.save(orderCaptor.capture())).thenAnswer(invocation -> {
            Order orderToSave = invocation.getArgument(0);
            orderToSave.setId(UUID.randomUUID());
            return orderToSave;
        });

        // 6. Giả lập map response (giống createOrder)
        CourseClient.CourseResponse courseResponse1 = new CourseClient.CourseResponse();
        courseResponse1.setData(Map.of("title", "Course 1 Title", "image", "img1.jpg"));
        when(courseClient.getCourseDetails(courseId1.toString())).thenReturn(courseResponse1);

        CourseClient.CourseResponse courseResponse2 = new CourseClient.CourseResponse();
        courseResponse2.setData(Map.of("title", "Course 2 Title", "image", "img2.jpg"));
        when(courseClient.getCourseDetails(courseId2.toString())).thenReturn(courseResponse2);
    }

    @Test
    void createOrderFromCart_ShouldCreateOrderAndClearCart_WhenClearCartIsTrue() {
        // --- Arrange ---
        // 1. Set request clear cart = true
        createFromCartRequest.setClearCartAfterOrder(true);

        // 2. Setup các mock cho trường hợp thành công
        setupMockSuccessCart();

        // 3. Giả lập việc save cart (sau khi clear)
        when(cartRepository.save(mockedCart)).thenReturn(mockedCart);

        // --- Act ---
        OrderResponse response = orderService.createOrderFromCart(createFromCartRequest, userId);

        // --- Assert ---
        // 1. Kiểm tra response trả về
        assertNotNull(response);
        assertEquals(2, response.getItems().size());
        assertEquals(0, new BigDecimal("150.0").compareTo(response.getFinalAmount())); // Tổng tiền lấy có lưu chính xác không

        // 2. Kiểm tra repository.save được gọi đúng 1 lần
        verify(orderRepository, times(1)).save(any(Order.class));

        Order savedOrder = orderCaptor.getValue();

        //3.  Kiểm tra các thuộc tính của Order được lưu
        assertEquals(userId, savedOrder.getUserId());
        assertEquals("Creating order from cart", savedOrder.getNotes());
        assertEquals(2, savedOrder.getItems().size());
        assertEquals(0, new BigDecimal("150.0").compareTo(savedOrder.getTotalAmount())); // Tổng tiền được set chính xác không

        // 3. Xác minh cart đã được clear và save
        verify(mockedCart, times(1)).clearItems();
        verify(cartRepository, times(1)).save(mockedCart);
    }

    @Test
    void createOrderFromCart_ShouldCreateOrderAndKeepCart_WhenClearCartIsFalse() {
        // --- Arrange ---
        // 1. Set request clear cart = false
        createFromCartRequest.setClearCartAfterOrder(false);

        // 2. Setup các mock cho trường hợp thành công
        setupMockSuccessCart();

        // --- Act ---
        OrderResponse response = orderService.createOrderFromCart(createFromCartRequest, userId);

        // --- Assert ---
        // 1. Kiểm tra response 
        assertNotNull(response);
        assertEquals(0, new BigDecimal("150.0").compareTo(response.getFinalAmount()));

        // 2. Kiểm tra Order đã lưu 
        verify(orderRepository, times(1)).save(any(Order.class));
        Order savedOrder = orderCaptor.getValue();
        assertEquals(0, new BigDecimal("150.0").compareTo(savedOrder.getTotalAmount()));

        // 3. Xác minh cart KHÔNG bị clear và KHÔNG bị save
        verify(mockedCart, never()).clearItems();
        verify(cartRepository, never()).save(mockedCart);
    }

    @Test
    void createOrderFromCart_ShouldThrowException_WhenCartNotFound() {
        // --- Arrange ---
        // 1. Giả lập `findByUserIdWithItems` trả về rỗng
        when(cartRepository.findByUserIdWithItems(userId)).thenReturn(Optional.empty());

        // --- Act & Assert ---
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrderFromCart(createFromCartRequest, userId);
        });
        // 1. Kiểm tra nội dung exception
        assertEquals("Cart not found", exception.getMessage());
        // 2. Kiểm tra orderRepository.save không được gọi
        verify(orderRepository, never()).save(any()); 
    }

    @Test
    void createOrderFromCart_ShouldThrowException_WhenCartIsEmpty() {
        // --- Arrange ---
        // 1. Giả lập tìm thấy cart
        when(cartRepository.findByUserIdWithItems(userId)).thenReturn(Optional.of(mockedCart));
        // 2. Giả lập cart rỗng
        when(mockedCart.isEmpty()).thenReturn(true);

        // --- Act & Assert ---
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrderFromCart(createFromCartRequest, userId);
        });

        assertEquals("Cannot create order from empty cart", exception.getMessage());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void createOrderFromCart_ShouldThrowException_WhenItemIsAlreadyPurchased() {
        // --- Arrange ---
        // 1. Giả lập tìm thấy cart, không rỗng, có 1 item
        when(cartRepository.findByUserIdWithItems(userId)).thenReturn(Optional.of(mockedCart));
        when(mockedCart.isEmpty()).thenReturn(false);
        when(mockedCart.getItems()).thenReturn(List.of(cartItem1));

        // 2. Giả lập user ĐÃ MUA course 1
        when(orderRepository.findByUserIdAndCourseIdAndStatusIn(eq(userId), eq(courseId1), anyList()))
                .thenReturn(Optional.of(new Order()));

        // --- Act & Assert ---
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrderFromCart(createFromCartRequest, userId);
        });

        // 1. Kiểm tra message lỗi
        assertTrue(exception.getMessage().contains("You have already purchased course: " + courseId1));
        // 2. Kiểm tra không tạo order & không clear cart
        verify(orderRepository, never()).save(any()); 
        verify(mockedCart, never()).clearItems(); 
        verify(cartRepository, never()).save(mockedCart);
    }

}