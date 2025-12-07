package com.example.Order_Photo.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CouponValidationResponse {
    private boolean valid;
    private String message;
    private String discountType;
    private BigDecimal discountValue;
    private String code;
}