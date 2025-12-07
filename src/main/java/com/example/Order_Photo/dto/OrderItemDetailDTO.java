package com.example.Order_Photo.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItemDetailDTO {
    private String serviceName;
    private String dimensions;
    private Integer quantity;
    private BigDecimal price;
    private String photoUrl;
}