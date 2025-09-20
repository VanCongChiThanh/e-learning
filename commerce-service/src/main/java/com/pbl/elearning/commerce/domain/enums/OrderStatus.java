package com.pbl.elearning.commerce.domain.enums;

public enum OrderStatus {
    PENDING, // Đang chờ thanh toán
    PAID, // Đã thanh toán
    FAILED, // Thanh toán thất bại
    CANCELLED, // Đã hủy
    REFUNDED, // Đã hoàn tiền
    DELIVERED // Đã giao hàng (đã cấp quyền truy cập khóa học)
}
