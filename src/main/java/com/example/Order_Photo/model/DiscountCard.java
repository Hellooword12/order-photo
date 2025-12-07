package com.example.Order_Photo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "discount_cards")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DiscountCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code; // Формат: XXXX-XXXX-XXXX

    @Column(nullable = false)
    private String cardName;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    @Column(nullable = false)
    private BigDecimal discountValue;

    private BigDecimal minOrderAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"password", "hibernateLazyInitializer", "handler"}) // Игнорируем только пароль
    private User user;

    private Integer totalOrders = 0;
    private BigDecimal totalSpent = BigDecimal.ZERO;
    private Integer usageCount = 0;
    private Integer usageLimit;

    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private boolean active = true;
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum DiscountType {
        PERCENTAGE, FIXED_AMOUNT
    }

    public boolean isValid() {
        if (!active) return false;

        LocalDateTime now = LocalDateTime.now();
        if (validFrom != null && now.isBefore(validFrom)) return false;
        if (validTo != null && now.isAfter(validTo)) return false;
        if (usageLimit != null && usageCount >= usageLimit) return false;

        return true;
    }
}