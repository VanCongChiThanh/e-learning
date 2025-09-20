package com.pbl.elearning.commerce.domain.enums;

public enum PaymentStatus {
    PENDING, // Đang chờ thanh toán
    PROCESSING, // Đang xử lý
    SUCCESS, // Thanh toán thành công
    FAILED, // Thanh toán thất bại
    CANCELLED, // Đã hủy
    REFUNDED // Đã hoàn tiền
}
