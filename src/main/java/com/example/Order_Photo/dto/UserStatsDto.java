package com.example.Order_Photo.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class UserStatsDto {
    private int totalOrders;
    private BigDecimal totalSpent;
    private DiscountCardDto discountCard;

    // статистика по карте
    private int ordersWithCard;
    private BigDecimal totalSaved;

    public UserStatsDto() {
        this.ordersWithCard = 0;
        this.totalSaved = BigDecimal.ZERO;
        this.totalSpent = BigDecimal.ZERO;
    }
}