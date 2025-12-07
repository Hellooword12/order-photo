package com.example.Order_Photo.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {
    private String customerEmail;
    private String customerPhone;
    private String couponCode;
    private List<OrderItemRequest> items;

    @Data
    public static class OrderItemRequest {
        private Long serviceId;
        private Long serviceSizeId;
        private Integer quantity;
        private String photoUrl;
    }
}