# PayOS Integration Guide - E-Learning Platform

Hướng dẫn tích hợp PayOS cho hệ thống thanh toán khóa học trực tuyến.

## Tổng quan

Hệ thống thanh toán PayOS đã được tích hợp vào module `commerce-service` với flow đầy đủ:

1. Tạo đơn hàng (Order)
2. Tạo payment link PayOS
3. Xử lý webhook từ PayOS
4. Cấp quyền truy cập khóa học

## Cấu trúc Database

### Bảng chính:

- `orders`: Lưu thông tin đơn hàng
- `order_items`: Chi tiết các khóa học trong đơn hàng
- `payments`: Thông tin thanh toán PayOS

### Enum Status:

- `OrderStatus`: PENDING, PAID, FAILED, CANCELLED, REFUNDED, DELIVERED
- `PaymentStatus`: PENDING, PROCESSING, SUCCESS, FAILED, CANCELLED, REFUNDED
- `PaymentMethod`: PAYOS, VNPAY, BANK_TRANSFER, WALLET

## Cấu hình PayOS

### 1. Environment Variables

```bash
# PayOS Configuration
PAYOS_CLIENT_ID=your-payos-client-id
PAYOS_API_KEY=your-payos-api-key
PAYOS_CHECKSUM_KEY=your-payos-checksum-key
PAYOS_PARTNER_CODE=your-partner-code
PAYOS_SANDBOX=true
PAYOS_DEFAULT_EXPIRATION=15
PAYOS_RETURN_URL=http://localhost:8080/api/payments/return/payos
PAYOS_CANCEL_URL=http://localhost:8080/api/payments/cancel/payos
PAYOS_WEBHOOK_URL=http://your-domain.com/api/payments/webhook/payos
```

### 2. Cấu hình Webhook PayOS

- URL: `http://your-domain.com/api/payments/webhook/payos`
- Method: POST
- Đảm bảo endpoint có thể truy cập từ internet

## API Endpoints

### Shopping Cart Management

#### 1. Thêm khóa học vào giỏ hàng

```http
POST /api/cart/add
Content-Type: application/json
Authorization: Bearer {token}

{
  "courseId": 1,
  "courseName": "React for Beginners",
  "coursePrice": 299000.0,
  "quantity": 1,
  "courseDescription": "Learn React from scratch",
  "courseThumbnail": "https://example.com/thumbnail.jpg",
  "instructorName": "John Doe",
  "instructorId": 123,
  "courseDurationMinutes": 1200,
  "courseLevel": "Beginner",
  "courseCategory": "Programming"
}
```

#### 2. Lấy giỏ hàng

```http
GET /api/cart
Authorization: Bearer {token}
```

#### 3. Cập nhật số lượng trong giỏ hàng

```http
PUT /api/cart/items/{courseId}
Content-Type: application/json
Authorization: Bearer {token}

{
  "quantity": 2
}
```

#### 4. Xóa khóa học khỏi giỏ hàng

```http
DELETE /api/cart/items/{courseId}
Authorization: Bearer {token}
```

#### 5. Áp dụng mã giảm giá

```http
POST /api/cart/coupon/apply
Content-Type: application/json
Authorization: Bearer {token}

{
  "couponCode": "DISCOUNT20"
}
```

#### 6. Kiểm tra số lượng items trong giỏ hàng

```http
GET /api/cart/count
Authorization: Bearer {token}
```

### Order Management

#### 1. Tạo đơn hàng từ giỏ hàng (Recommended)

```http
POST /api/orders/from-cart
Content-Type: application/json
Authorization: Bearer {token}

{
  "notes": "Gift for friend",
  "clearCartAfterOrder": true
}
```

#### 2. Tạo đơn hàng trực tiếp

```http
POST /api/orders
Content-Type: application/json
Authorization: Bearer {token}

{
  "items": [
    {
      "courseId": 1,
      "courseName": "React for Beginners",
      "coursePrice": 299000.0,
      "quantity": 1,
      "courseDescription": "Learn React from scratch",
      "courseThumbnail": "https://example.com/thumbnail.jpg",
      "instructorName": "John Doe"
    }
  ],
  "couponCode": "DISCOUNT10",
  "notes": "Gift for friend"
}
```

#### 3. Lấy thông tin đơn hàng

```http
GET /api/orders/{orderId}
Authorization: Bearer {token}
```

#### 4. Lấy danh sách đơn hàng

```http
GET /api/orders?page=0&size=10
Authorization: Bearer {token}
```

### Payment Management

#### 1. Tạo payment link PayOS

```http
POST /api/payments
Content-Type: application/json
Authorization: Bearer {token}

{
  "orderId": 1,
  "paymentMethod": "PAYOS",
  "description": "Payment for course order",
  "expirationMinutes": 15,
  "buyerName": "Nguyen Van A",
  "buyerEmail": "nguyenvana@email.com",
  "buyerPhone": "0123456789"
}
```

**Response:**

```json
{
  "code": 200,
  "message": "Payment created successfully",
  "data": {
    "id": 1,
    "orderCode": "123456789012",
    "amount": 299000.0,
    "status": "PENDING",
    "checkoutUrl": "https://pay.payos.vn/web/...",
    "qrCode": "https://pay.payos.vn/qr/...",
    "expiresAt": "2024-01-01T10:15:00Z"
  }
}
```

#### 2. Lấy thông tin payment

```http
GET /api/payments/order-code/{orderCode}
Authorization: Bearer {token}
```

#### 3. Hủy payment

```http
DELETE /api/payments/order-code/{orderCode}
Authorization: Bearer {token}
```

## Flow Thanh toán

### 1. Flow chính

```
User → Tạo Order → Tạo Payment → PayOS Payment Link
→ User thanh toán → PayOS Webhook → Cấp quyền khóa học
```

### 3. Chi tiết flow với Cart

1. **Thêm vào giỏ hàng**:

   - Validate khóa học có tồn tại
   - Kiểm tra user chưa mua khóa học này
   - Thêm hoặc cập nhật quantity trong cart
   - Tính toán tổng tiền và discount

2. **Áp dụng coupon** (Optional):

   - Validate mã coupon
   - Tính toán discount amount
   - Cập nhật final amount

3. **Tạo đơn hàng từ cart**:

   - Validate các khóa học trong cart
   - Kiểm tra user chưa mua các khóa học này
   - Convert cart items thành order items
   - Tạo order với status PENDING
   - Clear cart (optional)

4. **Tạo payment**:

   - Tạo payment record với PayOS order code
   - Gọi PayOS API tạo payment link
   - Trả về checkout URL và QR code

5. **User thanh toán**:

   - Redirect đến PayOS payment page
   - User thực hiện thanh toán

6. **PayOS Webhook**:

   - Nhận webhook từ PayOS
   - Verify signature (cần implement)
   - Update payment status
   - Update order status thành PAID
   - Gọi `grantCourseAccess()` để cấp quyền

7. **Cấp quyền khóa học**:
   - Update order status thành DELIVERED
   - Tạo enrollment record (cần tích hợp)
   - Gửi email thông báo (cần tích hợp)

## Testing

### 1. Test Cart to Order Flow

```bash
# 1. Thêm khóa học vào cart
curl -X POST http://localhost:8080/api/cart/add \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "courseId": 1,
    "courseName": "Test Course",
    "coursePrice": 100000.0,
    "quantity": 1
  }'

# 2. Áp dụng coupon
curl -X POST http://localhost:8080/api/cart/coupon/apply \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "couponCode": "DISCOUNT10"
  }'

# 3. Tạo order từ cart
curl -X POST http://localhost:8080/api/orders/from-cart \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "notes": "Test order from cart",
    "clearCartAfterOrder": true
  }'

# 4. Tạo payment
curl -X POST http://localhost:8080/api/payments \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "orderId": 1,
    "paymentMethod": "PAYOS"
  }'

# 5. Mô phỏng webhook PayOS
curl -X POST http://localhost:8080/api/payments/webhook/payos \
  -H "Content-Type: application/json" \
  -d '{
    "code": "00",
    "desc": "Success",
    "data": {
      "orderCode": "123456789012",
      "amount": 90000,
      "description": "Payment for order",
      "reference": "TXN123456",
      "transactionDateTime": "2024-01-01T10:00:00Z"
    },
    "signature": "test-signature"
  }'
```

### 2. Test Direct Order Flow

```bash
# 1. Tạo order trực tiếp
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "items": [{
      "courseId": 1,
      "courseName": "Test Course",
      "coursePrice": 100000.0,
      "quantity": 1
    }]
  }'

# 2. Tạo payment
curl -X POST http://localhost:8080/api/payments \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "orderId": 1,
    "paymentMethod": "PAYOS"
  }'

# 3. Mô phỏng webhook PayOS
curl -X POST http://localhost:8080/api/payments/webhook/payos \
  -H "Content-Type: application/json" \
  -d '{
    "code": "00",
    "desc": "Success",
    "data": {
      "orderCode": "123456789012",
      "amount": 100000,
      "description": "Payment for order",
      "reference": "TXN123456",
      "transactionDateTime": "2024-01-01T10:00:00Z"
    },
    "signature": "test-signature"
  }'
```

### 3. Test Cases

#### Cart Flow Test Cases:

1. **Happy Path**: Add to cart → Apply coupon → Create order → Payment → Webhook success → Grant access
2. **Empty Cart**: Attempt to create order from empty cart
3. **Duplicate Course in Cart**: Add same course multiple times
4. **Invalid Coupon**: Apply invalid coupon code
5. **Course Already Purchased**: Add purchased course to cart

#### Payment Test Cases:

6. **Payment Timeout**: Payment hết hạn
7. **Payment Cancelled**: User hủy payment
8. **Payment Failed**: Thanh toán thất bại
9. **Duplicate Order**: User mua lại khóa học đã có

#### Edge Cases:

10. **Cart Abandoned**: Test cart cleanup
11. **Concurrent Cart Updates**: Multiple simultaneous cart operations
12. **Large Cart**: Cart with many items

## Tích hợp thêm

### 1. Enrollment Service (TODO)

```java
// Trong OrderService.grantCourseAccess()
enrollmentService.grantCourseAccess(order.getUserId(), order.getItems());
```

### 2. Email Service (TODO)

```java
// Gửi email xác nhận mua hàng
emailService.sendPurchaseConfirmation(order);
```

### 3. Course Service Integration

```java
// Validate course availability
Course course = courseService.getCourseById(item.getCourseId());
if (!course.isAvailableForPurchase()) {
    throw new RuntimeException("Course not available");
}
```

### 4. User Authentication

```java
// Extract user ID from JWT token
private Long getUserIdFromAuthentication(Authentication authentication) {
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
    return userPrincipal.getId();
}
```

## Security Notes

1. **Webhook Verification**: Implement proper PayOS signature verification
2. **Authentication**: Ensure all endpoints require valid JWT token
3. **Authorization**: Users can only access their own orders/payments
4. **Input Validation**: Validate all request parameters
5. **SQL Injection**: Use JPA repositories (đã implement)

## Monitoring & Logging

1. **Cart Events**: Log cart additions, updates, abandonments
2. **Payment Events**: Log tất cả payment events
3. **Error Tracking**: Monitor failed payments và webhook errors
4. **Performance**: Track payment completion time, cart conversion rate
5. **Business Metrics**:
   - Cart abandonment rate
   - Average cart value
   - Most added/removed courses
   - Coupon usage statistics
   - Revenue, conversion rate, popular courses

## Production Checklist

### Core Payment Features:

- [ ] Cấu hình PayOS production credentials
- [ ] Setup webhook endpoint với HTTPS
- [ ] Implement webhook signature verification
- [ ] Configure proper error handling
- [ ] Test với PayOS sandbox environment

### Cart & Order Features:

- [ ] Implement cart cleanup job (scheduled task)
- [ ] Cart abandonment email notifications
- [ ] Optimize cart database queries
- [ ] Implement cart session persistence
- [ ] Test concurrent cart operations

### Integration Features:

- [ ] Tích hợp với course service for price validation
- [ ] Implement proper coupon service
- [ ] Tích hợp với enrollment service
- [ ] Setup inventory/availability checks
- [ ] Implement proper user authentication

### Monitoring & Performance:

- [ ] Setup monitoring và alerting
- [ ] Performance testing (cart operations)
- [ ] Cart analytics và metrics
- [ ] Database indexing optimization
- [ ] Cấu hình email notifications

### Security:

- [ ] Security review
- [ ] Input validation strengthening
- [ ] Rate limiting for cart operations
- [ ] Audit logging for sensitive operations

## Troubleshooting

### Common Issues:

1. **Webhook không nhận được**:

   - Kiểm tra URL accessible từ internet
   - Verify HTTPS certificate
   - Check firewall settings

2. **Payment status không update**:

   - Check webhook logs
   - Verify signature validation
   - Check database connections

3. **User không nhận được quyền truy cập**:
   - Check enrollment service integration
   - Verify order delivery status
   - Check email service logs

### Debug Tips:

```java
// Enable debug logging
logging.level.com.pbl.elearning.commerce=DEBUG
logging.level.vn.payos=DEBUG

// Cart debugging queries
SELECT c.*, ci.* FROM carts c
LEFT JOIN cart_items ci ON c.id = ci.cart_id
WHERE c.user_id = ?;

// Check cart calculations
SELECT
    c.user_id,
    c.total_items,
    c.total_amount,
    c.discount_amount,
    c.final_amount,
    SUM(ci.total_price) as calculated_total
FROM carts c
LEFT JOIN cart_items ci ON c.id = ci.cart_id
GROUP BY c.id;
```

## New Features Added

### Shopping Cart System:

- ✅ **Full CRUD Cart Operations**: Add, update, remove items
- ✅ **Cart Persistence**: Database-backed cart storage
- ✅ **Coupon Integration**: Apply/remove discount coupons
- ✅ **Duplicate Prevention**: Prevent duplicate courses in cart
- ✅ **Cart Statistics**: Total items, amount, savings calculation
- ✅ **Cart to Order**: Direct conversion from cart to order

### Enhanced Order Flow:

- ✅ **Multiple Order Creation**: From cart or direct
- ✅ **Better Validation**: Prevent purchasing owned courses
- ✅ **Improved Error Handling**: Comprehensive error messages
- ✅ **Transaction Safety**: Proper transaction boundaries

### API Enhancements:

- ✅ **RESTful Cart APIs**: 11 cart management endpoints
- ✅ **Enhanced Order APIs**: Added cart-to-order endpoint
- ✅ **Better DTOs**: Comprehensive request/response models
- ✅ **Swagger Documentation**: Complete API documentation
