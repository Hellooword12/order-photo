package com.example.Order_Photo.dto;

import com.example.Order_Photo.model.Service;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ServiceDTO {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private String iconClass;
    private String gradientFrom;
    private String gradientTo;
    private String buttonColor;
    private Integer displayOrder;
    private boolean active;
    private List<ServiceSizeDTO> sizes;

    public static ServiceDTO fromEntity(Service service) {
        ServiceDTO dto = new ServiceDTO();
        dto.setId(service.getId());
        dto.setName(service.getName());
        dto.setDescription(service.getDescription());
        dto.setImageUrl(service.getImageUrl());
        dto.setIconClass(service.getIconClass());
        dto.setGradientFrom(service.getGradientFrom());
        dto.setGradientTo(service.getGradientTo());
        dto.setButtonColor(service.getButtonColor());
        dto.setDisplayOrder(service.getDisplayOrder());
        dto.setActive(service.isActive());

        if (service.getSizes() != null) {
            dto.setSizes(service.getSizes().stream()
                    .map(ServiceSizeDTO::fromEntity)
                    .collect(Collectors.toList()));
        } else {
            dto.setSizes(new ArrayList<>());
        }

        return dto;
    }
}