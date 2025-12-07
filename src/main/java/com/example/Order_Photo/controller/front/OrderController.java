package com.example.Order_Photo.controller.front;

import com.example.Order_Photo.dto.*;
import com.example.Order_Photo.model.*;
import com.example.Order_Photo.model.OrderRequest;
import com.example.Order_Photo.repository.ServiceSizeRepository;
import com.example.Order_Photo.service.OrderService;
import com.example.Order_Photo.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final ServiceService serviceService;
    private final ServiceSizeRepository serviceSizeRepository;

        @PostMapping
        @ResponseBody
        public ResponseEntity<?> createOrder(
                @RequestBody OrderRequestNewDTO orderRequest,
                @AuthenticationPrincipal UserDetails userDetails) {

            try {
                System.out.println("=== ORDER CREATION REQUEST ===");
                System.out.println("Customer email: " + orderRequest.getCustomerEmail());
                System.out.println("Discount card code: " + orderRequest.getDiscountCardCode());
                System.out.println("Items count: " + orderRequest.getItems().size());

                // Преобразуем OrderRequestNewDTO в OrderRequestDTO (старый формат)
                OrderRequestDTO orderRequestDTO = convertNewToOldFormat(orderRequest);

                String username = userDetails != null ? userDetails.getUsername() : null;
                OrderResponseDTO order = orderService.createOrderWithAuthCheck(orderRequestDTO, username);

                return ResponseEntity.ok(Map.of("id", order.getId(), "message", "Order created successfully"));

            } catch (Exception e) {
                System.out.println("ERROR creating order: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
            }
        }

        private OrderRequestDTO convertNewToOldFormat(OrderRequestNewDTO newRequest) {
            OrderRequestDTO oldRequest = new OrderRequestDTO();
            oldRequest.setCustomerEmail(newRequest.getCustomerEmail());
            oldRequest.setCustomerPhone(newRequest.getCustomerPhone());
            oldRequest.setCouponCode(newRequest.getCouponCode());
            oldRequest.setDiscountCardCode(newRequest.getDiscountCardCode());

            List<OrderItemDTO> oldItems = newRequest.getItems().stream()
                    .map(newItem -> {
                        OrderItemDTO oldItem = new OrderItemDTO();
                        oldItem.setPhotoUrl(newItem.getPhotoUrl());
                        oldItem.setServices(newItem.getServices());
                        return oldItem;
                    })
                    .collect(Collectors.toList());

            oldRequest.setItems(oldItems);
            return oldRequest;
        }

        private BigDecimal calculateOrderTotal(OrderRequest orderRequest) {
            BigDecimal total = BigDecimal.ZERO;
            for (OrderItemRequest item : orderRequest.getItems()) {
                ServiceSize serviceSize = serviceSizeRepository.findById(item.getServiceSizeId())
                        .orElseThrow(() -> new RuntimeException("ServiceSize not found"));
                BigDecimal itemTotal = serviceSize.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                total = total.add(itemTotal);
            }
            return total;
        }

        // REST методы
        @PostMapping("/rest")
        @ResponseBody
        public ResponseEntity<OrderResponseDTO> createOrderRest(@Valid @RequestBody OrderRequestDTO orderRequest) {
            OrderResponseDTO order = orderService.createOrder(orderRequest);
            return ResponseEntity.ok(order);
        }


    }
