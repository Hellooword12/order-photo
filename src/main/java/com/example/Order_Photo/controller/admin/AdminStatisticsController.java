package com.example.Order_Photo.controller.admin;

import com.example.Order_Photo.model.Order;
import com.example.Order_Photo.repository.OrderRepository;
import com.example.Order_Photo.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminStatisticsController {

    private final OrderRepository orderRepository;
    private final ServiceRepository serviceRepository;

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();

        List<Order> allOrders = orderRepository.findAll();
        long totalOrders = allOrders.size();
        long pendingOrders = allOrders.stream()
                .filter(order -> order.getStatus() == Order.OrderStatus.PENDING)
                .count();

        BigDecimal totalRevenue = allOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalServices = serviceRepository.count();

        stats.put("totalOrders", totalOrders);
        stats.put("pendingOrders", pendingOrders);
        stats.put("totalServices", totalServices);
        stats.put("totalRevenue", totalRevenue);

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/orders/recent")
    public ResponseEntity<List<Order>> getRecentOrders() {
        List<Order> recentOrders = orderRepository.findAllByOrderByCreatedAtDesc();
        return ResponseEntity.ok(recentOrders.stream().limit(10).toList());
    }
}