package com.example.Order_Photo.model;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private String customerEmail;
    private String customerPhone;
    private String couponCode;
    private List<OrderItemRequest> items;
}
