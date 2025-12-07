package com.example.Order_Photo.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

@Data
public class OrderServiceRequestDTO {
    @NotNull
    private Long serviceId;

    @NotNull
    private Long serviceSizeId;

    @NotNull
    @Min(1)
    private Integer quantity;
}