// package com.pbl.elearning.commerce;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.pbl.elearning.commerce.config.PayOSConfig;
// import com.pbl.elearning.commerce.domain.Order;
// import com.pbl.elearning.commerce.domain.Payment;
// import com.pbl.elearning.commerce.domain.enums.OrderStatus;
// import com.pbl.elearning.commerce.domain.enums.PaymentStatus;
// import com.pbl.elearning.commerce.payload.request.CreatePaymentRequest;
// import com.pbl.elearning.commerce.payload.response.PaymentResponse;
// import com.pbl.elearning.commerce.repository.OrderRepository;
// import com.pbl.elearning.commerce.repository.PaymentRepository;
// import com.pbl.elearning.commerce.service.OrderService;
// import com.pbl.elearning.commerce.service.PaymentService;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.ArgumentCaptor;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.test.util.ReflectionTestUtils;
// import vn.payos.PayOS;
// import vn.payos.type.CheckoutResponseData;
// import vn.payos.type.PaymentData;

// import java.math.BigDecimal;
// import java.sql.Timestamp;
// import java.util.List;
// import java.util.Optional;
// import java.util.UUID;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class)
// class PaymentServiceTest {
// @Mock
// private PaymentRepository paymentRepository;
// @Mock
// private OrderRepository orderRepository;
// @Mock
// private PayOSConfig payOSConfig;
// @Mock
// private ObjectMapper objectMapper;
// @Mock
// private OrderService orderService;
// @Mock
// private PayOS mockedPayOS;

// @InjectMocks
// private PaymentService paymentService;

// private UUID userId;
// private UUID orderId;
// private Order mockOrder;
// private CreatePaymentRequest createPaymentRequest;
// private ArgumentCaptor<Payment> paymentCaptor;

// @BeforeEach
// void setUp() {
// userId = UUID.randomUUID();
// orderId = UUID.randomUUID();
// paymentCaptor = ArgumentCaptor.forClass(Payment.class);

// // Khởi tạo request mẫu
// createPaymentRequest = new CreatePaymentRequest();
// createPaymentRequest.setOrderId(orderId);
// createPaymentRequest.setDescription("Test Payment");
// createPaymentRequest.setExpirationMinutes(30);

// ReflectionTestUtils.setField(paymentService, "payOS", mockedPayOS);
// }

// /**
// * Test Cases cho createPayment
// */

// void setUpSuccessOrder() {
// mockOrder = mock(Order.class);
// when(mockOrder.getId()).thenReturn(orderId);
// when(mockOrder.getUserId()).thenReturn(userId);
// when(mockOrder.isPaid()).thenReturn(false);
// when(mockOrder.getTotalAmount()).thenReturn(new BigDecimal("150000"));
// when(mockOrder.getOrderNumber()).thenReturn("ORDER-12345");
// }

// void setUpPayOSConfig() {
// when(payOSConfig.getReturnUrl()).thenReturn("https://payos.return.url");
// when(payOSConfig.getCancelUrl()).thenReturn("https://payos.cancel.url");
// }

// @Test
// void createPayment_ShouldCreateNewPayment_WhenNoPendingPaymentExists() throws
// Exception {
// // --- Arrange ---
// setUpSuccessOrder();
// setUpPayOSConfig();

// // 1. Giả lập Order này có tồn tại & user sở hữu order
// when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));
// when(mockOrder.getUserId()).thenReturn(userId);
// when(mockOrder.getStatus()).thenReturn(OrderStatus.PENDING);

// // 2. Giả lập chưa có payment nào cho order này
// when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.empty());

// // 3. Giả lập việc tạo link PayOS thành công
// CheckoutResponseData payosResponse = mock(CheckoutResponseData.class);
// when(payosResponse.getCheckoutUrl()).thenReturn("http://payos.checkout.url");
// when(payosResponse.getPaymentLinkId()).thenReturn("link-123");
// when(payosResponse.getQrCode()).thenReturn("qr-code-data");
// when(payosResponse.getAccountNumber()).thenReturn("1208200405");
// when(payosResponse.getAccountName()).thenReturn("DINH BAO CHAU THI");

// when(mockedPayOS.createPaymentLink(any(PaymentData.class))).thenReturn(payosResponse);
// // 4. Giả lập lưu Payment
// when(paymentRepository.save(paymentCaptor.capture())).thenAnswer(inv ->
// inv.getArgument(0)); // lấy ra payment
// // được truyền vào

// // --- Act ---
// PaymentResponse response = paymentService.createPayment(createPaymentRequest,
// userId);

// // --- Assert ---
// // 1. Kiểm tra các trường của response trả về
// assertNotNull(response);
// assertEquals("http://payos.checkout.url", response.getCheckoutUrl());
// assertEquals("link-123", response.getPayosPaymentLinkId());
// assertEquals("qr-code-data", response.getQrCode());

// // 2. Kiểm tra paymenRepository.save được gọi đúng 2 lần và với trạng thái
// đúng
// // Lần 1: Tạo payment mới chưa có link
// // Lần 2: Cập nhật payment với link từ PayOS
// verify(paymentRepository, times(2)).save(any(Payment.class));

// List<Payment> savedPayments = paymentCaptor.getAllValues();
// Payment finalSavedPayment = savedPayments.get(1);

// // Kiểm tra lần save 2
// assertEquals(PaymentStatus.PENDING, finalSavedPayment.getStatus());
// assertEquals(mockOrder.getTotalAmount(), finalSavedPayment.getAmount());
// long expectedExpiry = System.currentTimeMillis() + (30 * 60 * 1000);
// assertTrue(Math.abs(finalSavedPayment.getExpiresAt().getTime() -
// expectedExpiry) < 5000);
// assertEquals("http://payos.checkout.url",
// finalSavedPayment.getCheckoutUrl()); // Đã có link
// assertEquals("link-123", finalSavedPayment.getPayosPaymentLinkId());
// assertEquals("1208200405", finalSavedPayment.getAccountNumber());
// }

// @Test
// void createPayment_ShouldThrowException_WhenOrderNotFound() throws Exception
// {
// // --- Arrange ---
// // 1. Giả lập Order không tồn tại
// when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

// // --- Act & Assert ---
// RuntimeException exception = assertThrows(RuntimeException.class, () -> {
// paymentService.createPayment(createPaymentRequest, userId);
// });

// // 1. Kiểm tra nội dung của exception
// assertEquals("Order not found", exception.getMessage());
// // 2. Đảm bảo payment không được lưu
// verify(paymentRepository, never()).save(any());
// verify(mockedPayOS, never()).createPaymentLink(any());
// }

// @Test
// void createPayment_ShouldThrowException_WhenUserIsNotOrderOwner() {
// // --- Arrange ---
// mockOrder = mock(Order.class);

// // 0. Giả lập Order này có tồn tại
// when(orderRepository.findById(orderId)).thenReturn((Optional.of(mockOrder)));

// // 1. Giả lập user tạo payment không phải là người sở hữu Order
// UUID otherUserId = UUID.randomUUID();
// when(mockOrder.getUserId()).thenReturn(otherUserId);

// // Act & Assert
// RuntimeException exception = assertThrows(RuntimeException.class, () -> {
// paymentService.createPayment(createPaymentRequest, userId);
// });

// // 1. Kiểm tra nội dung của exception
// assertEquals("Access denied to this order", exception.getMessage());
// // 2. Đảm bảo payment không được lưu
// verify(paymentRepository, never()).save(any());
// }

// @Test
// void createPayment_ShouldThrowException_WhenOrderIsAlreadyPaid() {
// // --- Arrange ---
// mockOrder = mock(Order.class);

// // 0. Giả lập Order có tồn tại & user sở hữu order
// when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));
// when(mockOrder.getUserId()).thenReturn(userId);

// // 1. Giả lập order đã được thanh toán
// when(mockOrder.isPaid()).thenReturn(true);

// // Act & Assert
// RuntimeException exception = assertThrows(RuntimeException.class, () -> {
// paymentService.createPayment(createPaymentRequest, userId);
// });

// assertEquals("Order already paid", exception.getMessage());
// verify(paymentRepository, never()).save(any());
// }

// @Test
// void
// createPayment_ShouldReturnExistingPayment_WhenPendingPaymentExistsAndIsNotExpired()
// throws Exception {
// // --- Arrange ---
// setUpSuccessOrder();

// // 0. Giả lập Order có tồn tại & user sở hữu order
// when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));
// when(mockOrder.getUserId()).thenReturn(userId);
// when(mockOrder.getStatus()).thenReturn(OrderStatus.PENDING);

// // 1. Giả lập đã có payment PENDING chưa hết hạn
// Payment existingPayment = new Payment();
// existingPayment.setStatus(PaymentStatus.PENDING);
// existingPayment.setExpiresAt(new Timestamp(System.currentTimeMillis() +
// 600000));
// existingPayment.setCheckoutUrl("http://payos.existing.url");
// existingPayment.setOrder(mockOrder);

// when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(existingPayment));

// // --- Act ---
// PaymentResponse response = paymentService.createPayment(createPaymentRequest,
// userId);

// // Assert
// assertNotNull(response);

// // 1. Kiểm tra trả về đúng link của payment tồn tại
// assertEquals("http://payos.existing.url", response.getCheckoutUrl());

// // 2. Đảm bảo không tạo payment mới
// verify(paymentRepository, never()).save(any());
// verify(mockedPayOS, never()).createPaymentLink(any());
// }

// @Test
// void createPayment_ShouldCreateNewPayment_WhenExistingPaymentIsExpired()
// throws Exception {
// // --- Arrange ---
// setUpSuccessOrder();
// setUpPayOSConfig();

// // 0. Giả lập Order có tồn tại & user sở hữu order
// when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));
// when(mockOrder.getUserId()).thenReturn(userId);
// when(mockOrder.getStatus()).thenReturn(OrderStatus.PENDING);

// // 1. Giả lập đã có payment PENDING nhưng đã hết hạn
// Payment expiredPayment = new Payment();
// expiredPayment.setStatus(PaymentStatus.PENDING);
// expiredPayment.setExpiresAt(new Timestamp(System.currentTimeMillis() -
// 60000));
// expiredPayment.setOrder(mockOrder);

// when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(expiredPayment));

// // Giả lập việc tạo link PayOS thành công cho payment MỚI
// CheckoutResponseData payosResponse = mock(CheckoutResponseData.class);
// when(payosResponse.getCheckoutUrl()).thenReturn("http://payos.new.checkout.url");
// when(payosResponse.getPaymentLinkId()).thenReturn("new-link-id");
// when(mockedPayOS.createPaymentLink(any(PaymentData.class))).thenReturn(payosResponse);

// // Giả lập save
// when(paymentRepository.save(paymentCaptor.capture())).thenAnswer(inv ->
// inv.getArgument(0));

// // --- Act ---
// PaymentResponse response = paymentService.createPayment(createPaymentRequest,
// userId);

// // -- Assert ---
// assertNotNull(response);
// // 1. Kiểm tra trả về link của payment mới
// assertEquals("http://payos.new.checkout.url", response.getCheckoutUrl());

// // 1. Kiểm tra paymentRepository.save được gọi đúng 3 lần
// // Lần 1: Save payment hết hạn -> CANCELLED
// // Lần 2: Save payment mới -> PENDING (trước khi gọi PayOS)
// // Lần 3: Save payment mới -> PENDING (sau khi có data từ PayOS)
// verify(paymentRepository, times(3)).save(any(Payment.class));

// List<Payment> savedPayments = paymentCaptor.getAllValues();
// // Kiểm tra lần save 1 (payment cũ)
// assertEquals(PaymentStatus.CANCELLED, savedPayments.get(0).getStatus());

// // Kiểm tra lần save 3 (payment mới)
// assertEquals(PaymentStatus.PENDING, savedPayments.get(2).getStatus());
// assertEquals("http://payos.new.checkout.url",
// savedPayments.get(2).getCheckoutUrl());
// }

// @Test
// void
// createPayment_ShouldThrowExceptionAndSaveFailedPayment_WhenPayOSApiFails()
// throws Exception {
// // --- Arrange ---
// setUpSuccessOrder();
// setUpPayOSConfig();

// // 1. Giả lập Order có tồn tại & user sở hữu order
// when(orderRepository.findById(orderId)).thenReturn(Optional.of(mockOrder));
// when(mockOrder.getUserId()).thenReturn(userId);

// // 2. Giả lập PayOS ném lỗi
// when(mockedPayOS.createPaymentLink(any(PaymentData.class)))
// .thenThrow(new Exception("PayOS API Error"));

// // 3. Giả lập save
// when(paymentRepository.save(paymentCaptor.capture())).thenAnswer(inv ->
// inv.getArgument(0));

// // --- Act & Assert ---
// RuntimeException exception = assertThrows(RuntimeException.class, () -> {
// paymentService.createPayment(createPaymentRequest, userId);
// });

// // 1. Kiểm tra nội dung của exception
// assertTrue(exception.getMessage().contains("Failed to create payment link:
// PayOS API Error"));

// // 2. Kiểm tra paymentRepository.save được gọi đúng 2 lần
// // Lần 1. Save payment mới -> PENDING (trước khi gọi PayOS)
// // Lần 2. Save payment mới -> FAILED (sau khi catch lỗi)
// verify(paymentRepository, times(2)).save(any(Payment.class));

// // 3. Kiểm tra trạng thái của payment sau khi lỗi
// List<Payment> savedPayments = paymentCaptor.getAllValues();
// Payment finalSavedPayment = savedPayments.get(1);
// assertEquals(PaymentStatus.FAILED, finalSavedPayment.getStatus()); // Quan
// trọng
// }
// }