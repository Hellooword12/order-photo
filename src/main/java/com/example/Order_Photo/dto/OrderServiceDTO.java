// OrderServiceDTO.java - НОВЫЙ КЛАСС
package com.example.Order_Photo.dto;

import lombok.Data;

@Data
public class OrderServiceDTO {
    private Long serviceId;
    private Long serviceSizeId;
    private Integer quantity;
}