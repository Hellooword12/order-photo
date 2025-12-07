package com.example.Order_Photo.dto;

import lombok.Data;
import jakarta.validation.Valid;
import java.util.List;

@Data
public class OrderItemDTO {
    private String photoUrl;

    @Valid
    private List<OrderServiceRequestDTO> services;
}