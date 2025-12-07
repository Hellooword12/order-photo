package com.example.Order_Photo.dto;

import com.example.Order_Photo.model.DiscountCard;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DiscountCardDto {
    private Long id;
    private String code;
    private String cardName;
    private String discountType;
    private BigDecimal discountValue;
    private Long userId;
    private String userName;
    private String userEmail;
    private Integer totalOrders;
    private BigDecimal totalSpent;
    private Integer usageCount;
    private Integer usageLimit;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private boolean active;
    private LocalDateTime createdAt;

    public static DiscountCardDto fromEntity(DiscountCard card) {
        if (card == null) return null;

        DiscountCardDto dto = new DiscountCardDto();
        dto.setId(card.getId());
        dto.setCode(card.getCode());
        dto.setCardName(card.getCardName());
        dto.setDiscountType(card.getDiscountType() != null ? card.getDiscountType().name() : null);        dto.setDiscountValue(card.getDiscountValue());
        dto.setTotalOrders(card.getTotalOrders());
        dto.setTotalSpent(card.getTotalSpent());
        dto.setUsageCount(card.getUsageCount() != null ? card.getUsageCount() : 0);
        dto.setUsageLimit(card.getUsageLimit() != null ? card.getUsageLimit() : 1);
        dto.setValidFrom(card.getValidFrom());
        dto.setValidTo(card.getValidTo());
        dto.setActive(card.isActive());
        dto.setCreatedAt(card.getCreatedAt());

        if (card.getUser() != null) {
            dto.setUserId(card.getUser().getId());
            dto.setUserName(card.getUser().getName());
            dto.setUserEmail(card.getUser().getEmail());
        }

        return dto;
    }

    public DiscountCard toEntity() {
        DiscountCard card = new DiscountCard();
        card.setId(this.id);
        card.setCode(this.code);
        card.setCardName(this.cardName);
        card.setDiscountType(DiscountCard.DiscountType.valueOf(this.discountType));
        card.setDiscountValue(this.discountValue);
        card.setTotalOrders(this.totalOrders);
        card.setTotalSpent(this.totalSpent);
        card.setUsageCount(this.usageCount);
        card.setUsageLimit(this.usageLimit);
        card.setValidFrom(this.validFrom);
        card.setValidTo(this.validTo);
        card.setActive(this.active);

        return card;
    }
}