package com.example.Order_Photo.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DiscountCardStatsDTO {
    private Long totalOrders;
    private BigDecimal totalSpent;

    // Активная скидочная карта
    private DiscountCardInfoDTO activeCard;

    // Статистика по активной карте
    private CardUsageStatsDTO cardStats;

    @Data
    public static class DiscountCardInfoDTO {
        private String code;
        private String cardName;
        private BigDecimal discountValue;
        private String discountType;
        private Integer usageCount;
        private Integer usageLimit;
    }

    @Data
    public static class CardUsageStatsDTO {
        private Long ordersWithCard;
        private BigDecimal totalSaved;
    }
}