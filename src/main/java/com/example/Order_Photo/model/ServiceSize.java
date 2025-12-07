package com.example.Order_Photo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "service_sizes")
public class ServiceSize {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Размер обязателен")
    @Column(nullable = false)
    private String size;

    @Column(nullable = false)
    private String sizeName;

    @NotNull(message = "Цена обязательна")
    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    @JsonIgnoreProperties({"sizes", "hibernateLazyInitializer", "handler"})
    private Service service;

    private boolean active = true;

}