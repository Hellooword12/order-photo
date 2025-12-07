package com.example.Order_Photo.dto;

import lombok.Data;
import jakarta.validation.Valid;
import java.util.List;

@Data
public class OrderRequestNewDTO {
    private String customerEmail;
    private String customerPhone;
    private String couponCode;
    private String discountCardCode;

    @Valid
    private List<OrderItemNewDTO> items;
}