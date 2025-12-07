package com.example.Order_Photo.dto;

import com.example.Order_Photo.model.ServiceSize;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServiceSizeDTO {
    private Long id;
    private String size;
    private String sizeName;
    private BigDecimal price;
    private Integer displayOrder;
    private boolean active;

    public static ServiceSizeDTO fromEntity(ServiceSize size) {
        ServiceSizeDTO dto = new ServiceSizeDTO();
        dto.setId(size.getId());
        dto.setSize(size.getSize());
        dto.setSizeName(size.getSizeName());
        dto.setPrice(size.getPrice());
        dto.setDisplayOrder(size.getDisplayOrder());
        dto.setActive(size.isActive());
        return dto;
    }
}