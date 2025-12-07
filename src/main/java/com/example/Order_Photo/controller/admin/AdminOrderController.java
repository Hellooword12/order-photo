package com.example.Order_Photo.controller.admin;

import com.example.Order_Photo.dto.OrderAdminResponseDTO;
import com.example.Order_Photo.model.Order;
import com.example.Order_Photo.repository.UserRepository;
import com.example.Order_Photo.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;
    private final com.example.Order_Photo.repository.OrderRepository orderRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Order.OrderStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Order> ordersPage;

        if (status != null && dateFrom != null && dateTo != null) {
            LocalDateTime startDateTime = dateFrom.atStartOfDay();
            LocalDateTime endDateTime = dateTo.atTime(LocalTime.MAX);
            ordersPage = orderRepository.findByStatusAndCreatedAtBetween(status, startDateTime, endDateTime, pageable);
        } else if (status != null) {
            ordersPage = orderRepository.findByStatus(status, pageable);
        } else if (dateFrom != null && dateTo != null) {
            LocalDateTime startDateTime = dateFrom.atStartOfDay();
            LocalDateTime endDateTime = dateTo.atTime(LocalTime.MAX);
            ordersPage = orderRepository.findByCreatedAtBetween(startDateTime, endDateTime, pageable);
        } else {
            ordersPage = orderRepository.findAll(pageable);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("content", ordersPage.getContent().stream()
                .map(OrderAdminResponseDTO::fromEntity)
                .collect(Collectors.toList()));
        response.put("currentPage", ordersPage.getNumber());
        response.put("totalItems", ordersPage.getTotalElements());
        response.put("totalPages", ordersPage.getTotalPages());
        response.put("size", ordersPage.getSize());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@PathVariable Long id) {
        try {
            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Заказ не найден"));

            OrderAdminResponseDTO orderDTO = OrderAdminResponseDTO.fromEntity(order);
            return ResponseEntity.ok(orderDTO);

        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String statusStr = request.get("status");
            Order.OrderStatus status = Order.OrderStatus.valueOf(statusStr.toUpperCase());

            Order order = orderRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Заказ не найден"));

            order.setStatus(status);
            orderRepository.save(order);

            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Неверный статус заказа");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getOrderStats() {
        Map<String, Object> stats = new HashMap<>();

        // Общее количество заказов
        long totalOrders = orderRepository.count();

        // Заказы по статусам
        long pendingOrders = orderRepository.countByStatus(Order.OrderStatus.PENDING);
        long processingOrders = orderRepository.countByStatus(Order.OrderStatus.PROCESSING);
        long completedOrders = orderRepository.countByStatus(Order.OrderStatus.COMPLETED);
        long cancelledOrders = orderRepository.countByStatus(Order.OrderStatus.CANCELLED);

        stats.put("totalOrders", totalOrders);
        stats.put("pendingOrders", pendingOrders);
        stats.put("processingOrders", processingOrders);
        stats.put("completedOrders", completedOrders);
        stats.put("cancelledOrders", cancelledOrders);

        return ResponseEntity.ok(stats);
    }
}