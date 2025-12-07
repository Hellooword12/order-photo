package com.example.Order_Photo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Entity
@Table(name = "services")
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Название услуги обязательно")
    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String dimensions;

    @Column(nullable = true)
    private BigDecimal price;

    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "icon_class")
    private String iconClass;

    @Column(name = "gradient_from")
    private String gradientFrom;

    @Column(name = "gradient_to")
    private String gradientTo;

    @Column(name = "button_color")
    private String buttonColor;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    private boolean active = true;

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"service", "hibernateLazyInitializer", "handler"})
    private List<ServiceSize> sizes = new ArrayList<>();

    public BigDecimal getPrice() {

        return getMinPrice();
    }

    public void setSizes(List<ServiceSize> sizes) {
        this.sizes.clear();
        if (sizes != null) {
            this.sizes.addAll(sizes);
            // связь для каждого размера
            for (ServiceSize size : sizes) {
                size.setService(this);
            }
        }
    }

    // Метод для удобного добавления размера
    public void addSize(ServiceSize size) {
        sizes.add(size);
        size.setService(this);
    }

    // Метод для удаления размера
    public void removeSize(ServiceSize size) {
        sizes.remove(size);
        size.setService(null);
    }

    // Геттер для минимальной цены
    public BigDecimal getMinPrice() {
        return sizes.stream()
                .filter(ServiceSize::isActive)
                .map(ServiceSize::getPrice)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    // Геттер для размеров в виде строки
    public String getDimensionsString() {
        return sizes.stream()
                .filter(ServiceSize::isActive)
                .map(ServiceSize::getSizeName)
                .collect(Collectors.joining(", "));
    }
}