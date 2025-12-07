package com.example.Order_Photo.model;

import lombok.Data;

@Data
public class OrderItemRequest {
    private Long serviceId;
    private Long serviceSizeId;
    private Integer quantity;
    private String photoUrl;
}