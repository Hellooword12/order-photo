package com.example.Order_Photo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"orders", "password", "hibernateLazyInitializer", "handler"})
    private User user;

    @Column(nullable = false)
    private String customerEmail;

    @Column(nullable = false)
    private String customerPhone;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"order", "hibernateLazyInitializer", "handler"})
    private List<OrderItem> items = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "coupon_id")
    @JsonIgnoreProperties({"user", "hibernateLazyInitializer", "handler"})
    private DiscountCard coupon;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "used_discount_card_id")
    @JsonIgnoreProperties({"user", "hibernateLazyInitializer", "handler"})
    private DiscountCard usedDiscountCard;

    private String couponCode;

    // МЕТОД ДЛЯ РАСЧЕТА ФИНАЛЬНОЙ СУММЫ
    public BigDecimal getFinalAmount() {
        BigDecimal calculatedAmount = totalAmount != null ? totalAmount : BigDecimal.ZERO;

        if (discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) > 0) {
            calculatedAmount = calculatedAmount.subtract(discountAmount);
            if (calculatedAmount.compareTo(BigDecimal.ZERO) < 0) {
                calculatedAmount = BigDecimal.ZERO;
            }
        }

        return calculatedAmount;
    }

    // ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ
    public boolean hasDiscount() {
        return discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasCoupon() {
        return coupon != null || (couponCode != null && !couponCode.trim().isEmpty());
    }

    public void setOrderDate(LocalDateTime now) {
        this.createdAt = now;
    }

    public enum OrderStatus {
        PENDING, PROCESSING, COMPLETED, CANCELLED
    }
}