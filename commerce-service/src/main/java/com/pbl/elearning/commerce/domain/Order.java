package com.pbl.elearning.commerce.domain;

import com.pbl.elearning.commerce.domain.enums.OrderStatus;
import com.pbl.elearning.common.domain.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order extends AbstractEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "order_number", unique = true, nullable = false)
    private String orderNumber;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "notes")
    private String notes;

    @Column(name = "delivered_at")
    private Timestamp deliveredAt;

    // One-to-Many relationship with OrderItem
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> items = new ArrayList<>();

    // One-to-One relationship with Payment
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Payment payment;

    // Helper methods
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
    }

    public void calculateTotalAmount() {
        this.totalAmount = items.stream()
                .map(OrderItem::getUnitPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean isPaid() {
        return OrderStatus.PAID.equals(this.status) || OrderStatus.DELIVERED.equals(this.status);
    }

    public boolean isDelivered() {
        return OrderStatus.DELIVERED.equals(this.status);
    }

    public int getTotalItems() {
        return items.size();
    }
}
