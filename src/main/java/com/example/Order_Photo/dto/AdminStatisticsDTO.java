package com.example.Order_Photo.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class AdminStatisticsDTO {
    private Long totalOrders;
    private Long pendingOrders;
    private Long totalServices;
    private BigDecimal totalRevenue;

    public AdminStatisticsDTO(Long totalOrders, Long pendingOrders, Long totalServices, BigDecimal totalRevenue) {
        this.totalOrders = totalOrders;
        this.pendingOrders = pendingOrders;
        this.totalServices = totalServices;
        this.totalRevenue = totalRevenue;
    }
}