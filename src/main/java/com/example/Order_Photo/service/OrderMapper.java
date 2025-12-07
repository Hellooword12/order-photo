package com.example.Order_Photo.service;

import com.example.Order_Photo.dto.OrderResponseDTO;
import com.example.Order_Photo.model.Order;
import com.example.Order_Photo.model.OrderItem;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public OrderResponseDTO toDTO(Order order) {
        if (order == null) {
            return null;
        }

        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setCustomerEmail(order.getCustomerEmail());
        dto.setCustomerPhone(order.getCustomerPhone());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setStatus(order.getStatus().name());
        dto.setCreatedAt(order.getCreatedAt());

        if (order.getItems() != null) {
            dto.setItems(order.getItems().stream()
                    .map(this::convertItemToSimpleDTO)
                    .collect(Collectors.toList()));
        }

        if (order.getCoupon() != null) {
            dto.setCouponCode(order.getCoupon().getCode());
        }

        return dto;
    }

    private OrderResponseDTO.OrderItemSimpleDTO convertItemToSimpleDTO(OrderItem item) {
        OrderResponseDTO.OrderItemSimpleDTO dto = new OrderResponseDTO.OrderItemSimpleDTO();
        dto.setServiceName(item.getService().getName());
        dto.setDimensions(item.getService().getDimensions());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getService().getPrice());
        dto.setPhotoUrl(item.getPhotoUrl());
        return dto;
    }
}