package com.example.Order_Photo.dto;

import lombok.Data;

@Data
public class DiscountCardValidationResult {
    private boolean valid;
    private String message;
    private DiscountCardDto card;

    public DiscountCardValidationResult(boolean valid, String message, DiscountCardDto card) {
        this.valid = valid;
        this.message = message;
        this.card = card;
    }

    public DiscountCardValidationResult(boolean valid, String message) {
        this(valid, message, null);
    }
}