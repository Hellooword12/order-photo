package com.example.Order_Photo.dto;

import com.example.Order_Photo.model.OrderItem;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItemAdminDTO {
    private Long id;
    private String serviceName;
    private String sizeName;
    private String dimensions;
    private BigDecimal price;
    private Integer quantity;
    private String photoUrl;
    private BigDecimal itemTotal;

    public static OrderItemAdminDTO fromEntity(OrderItem item) {
        if (item == null) return null;

        OrderItemAdminDTO dto = new OrderItemAdminDTO();
        dto.setId(item.getId());
        dto.setQuantity(item.getQuantity());
        dto.setPhotoUrl(item.getPhotoUrl());

        // Безопасное извлечение данных об услуге
        if (item.getService() != null) {
            dto.setServiceName(item.getService().getName());
        } else {
            dto.setServiceName("Услуга не найдена");
        }

        // Безопасное извлечение данных о размере услуги
        if (item.getServiceSize() != null) {
            dto.setSizeName(item.getServiceSize().getSizeName());
            dto.setDimensions(item.getServiceSize().getSize()); // используем поле size для dimensions
            dto.setPrice(item.getServiceSize().getPrice());

            // Расчет общей стоимости для позиции
            if (item.getQuantity() != null && item.getServiceSize().getPrice() != null) {
                dto.setItemTotal(item.getServiceSize().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())));
            } else {
                dto.setItemTotal(BigDecimal.ZERO);
            }
        } else {
            dto.setSizeName("N/A");
            dto.setDimensions("N/A");
            dto.setPrice(BigDecimal.ZERO);
            dto.setItemTotal(BigDecimal.ZERO);
        }

        return dto;
    }
}