package com.example.Order_Photo.dto;

import com.example.Order_Photo.model.DiscountCard;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CouponDto {
    private Long id;
    private String code;
    private DiscountCard.DiscountType discountType;
    private BigDecimal discountValue;
    private Integer usageLimit;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private boolean active;

    public DiscountCard toEntity() {
        DiscountCard coupon = new DiscountCard();
        coupon.setId(this.id);
        coupon.setCode(this.code);
        coupon.setDiscountType(this.discountType);
        coupon.setDiscountValue(this.discountValue);
        coupon.setUsageLimit(this.usageLimit);
        coupon.setValidFrom(this.validFrom);
        coupon.setValidTo(this.validTo);
        coupon.setActive(this.active);
        return coupon;
    }

    public static CouponDto fromEntity(DiscountCard coupon) {
        CouponDto dto = new CouponDto();
        dto.setId(coupon.getId());
        dto.setCode(coupon.getCode());
        dto.setDiscountType(coupon.getDiscountType());
        dto.setDiscountValue(coupon.getDiscountValue());
        dto.setUsageLimit(coupon.getUsageLimit());
        dto.setValidFrom(coupon.getValidFrom());
        dto.setValidTo(coupon.getValidTo());
        dto.setActive(coupon.isActive());
        return dto;
    }
}