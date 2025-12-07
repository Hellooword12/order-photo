package com.example.Order_Photo.dto;

import com.example.Order_Photo.model.Order;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class OrderAdminResponseDTO {
    private Long id;
    private String customerEmail;
    private String customerPhone;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private String status;
    private LocalDateTime createdAt;
    private String couponCode;
    private Long userId;
    private String userName;
    private String userUsername;
    private List<OrderItemAdminDTO> items;

    public static OrderAdminResponseDTO fromEntity(Order order) {
        if (order == null) return null;

        OrderAdminResponseDTO dto = new OrderAdminResponseDTO();
        dto.setId(order.getId());
        dto.setCustomerEmail(order.getCustomerEmail());
        dto.setCustomerPhone(order.getCustomerPhone());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setStatus(order.getStatus() != null ? order.getStatus().name() : null);
        dto.setCreatedAt(order.getCreatedAt());
        dto.setCouponCode(order.getCouponCode());

        if (order.getUser() != null) {
            dto.setUserId(order.getUser().getId());
            dto.setUserName(order.getUser().getName());
            dto.setUserUsername(order.getUser().getUsername());
        }

        if (order.getItems() != null) {
            dto.setItems(order.getItems().stream()
                    .map(OrderItemAdminDTO::fromEntity)
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}