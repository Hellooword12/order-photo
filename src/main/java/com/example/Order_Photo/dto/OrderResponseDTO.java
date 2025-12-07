package com.example.Order_Photo.dto;

import com.example.Order_Photo.model.Order;
import com.example.Order_Photo.model.OrderItem;
import com.example.Order_Photo.model.DiscountCard;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class OrderResponseDTO {
    private Long id;
    private String customerEmail;
    private String customerPhone;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private String status;
    private DiscountCardSimpleDTO usedDiscountCard;
    private BigDecimal finalAmount;
    private LocalDateTime createdAt;
    private String couponCode;
    private CouponDTO coupon;
    private List<OrderItemSimpleDTO> items;
    private Long userId;
    private String userName;
    private String userUsername;

    public static OrderResponseDTO fromEntity(Order order) {
        if (order == null) return null;

        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setCustomerEmail(order.getCustomerEmail());
        dto.setCustomerPhone(order.getCustomerPhone());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setStatus(order.getStatus() != null ? order.getStatus().name() : null);
        dto.setUsedDiscountCard(DiscountCardSimpleDTO.fromEntity(order.getUsedDiscountCard()));
        dto.setCreatedAt(order.getCreatedAt());

        dto.setFinalAmount(order.getFinalAmount());

        if (order.getCoupon() != null) {
            dto.setCouponCode(order.getCoupon().getCode());
            dto.setCoupon(CouponDTO.fromEntity(order.getCoupon()));
        }

        // Конвертация items
        if (order.getItems() != null) {
            dto.setItems(order.getItems().stream()
                    .map(OrderItemSimpleDTO::fromEntity)
                    .collect(Collectors.toList()));
        }

        // Информация о пользователе
        if (order.getUser() != null) {
            dto.setUserId(order.getUser().getId());
            dto.setUserName(order.getUser().getName());
            dto.setUserUsername(order.getUser().getUsername());
        }

        return dto;
    }

    public DiscountCardSimpleDTO getDiscountCard() {
        return this.usedDiscountCard;
    }


    @Data
    public static class DiscountCardSimpleDTO {
        private Long id;
        private String code;
        private String cardName;
        private String discountType;
        private BigDecimal discountValue;

        public static DiscountCardSimpleDTO fromEntity(DiscountCard card) {
            if (card == null) return null;

            DiscountCardSimpleDTO dto = new DiscountCardSimpleDTO();
            dto.setId(card.getId());
            dto.setCode(card.getCode());
            dto.setCardName(card.getCardName());
            dto.setDiscountType(card.getDiscountType() != null ? card.getDiscountType().name() : null);
            dto.setDiscountValue(card.getDiscountValue());
            return dto;
        }
    }

    @Data
    public static class OrderItemSimpleDTO {
        private String serviceName;
        private String dimensions;
        private Integer quantity;
        private BigDecimal price;
        private String photoUrl;
        private String sizeName;

        public static OrderItemSimpleDTO fromEntity(OrderItem item) {
            if (item == null) return null;

            OrderItemSimpleDTO dto = new OrderItemSimpleDTO();
            dto.setServiceName(item.getService() != null ? item.getService().getName() : null);
            dto.setDimensions(item.getServiceSize() != null ? item.getServiceSize().getSize() : null);
            dto.setQuantity(item.getQuantity());
            dto.setPrice(item.getServiceSize() != null ? item.getServiceSize().getPrice() : null);
            dto.setPhotoUrl(item.getPhotoUrl());
            dto.setSizeName(item.getServiceSize() != null ? item.getServiceSize().getSizeName() : null);
            return dto;
        }
    }

    @Data
    public static class CouponDTO {
        private String code;
        private String discountType;
        private BigDecimal discountValue;

        public static CouponDTO fromEntity(DiscountCard coupon) {
            if (coupon == null) return null;

            CouponDTO dto = new CouponDTO();
            dto.setCode(coupon.getCode());
            dto.setDiscountType(coupon.getDiscountType() != null ? coupon.getDiscountType().name() : null);
            dto.setDiscountValue(coupon.getDiscountValue());
            return dto;
        }
    }
}