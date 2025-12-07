package com.example.Order_Photo.controller.admin;

import com.example.Order_Photo.dto.OrderDashboardDTO;
import com.example.Order_Photo.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Controller
public class AdminPageController {

    private final OrderService orderService;

    @GetMapping("/admin")
    public String adminDashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/admin/settings")
    public String adminSettings() {
        return "admin/settings";
    }

    @GetMapping("/admin/discount-cards")
    public String adminDuscountCards() {
        return "admin/discount-cards";
    }

    @GetMapping("/admin/orders")
    public String adminOrders() {
        return "admin/orders";
    }

    @GetMapping("/admin/coupons")
    public String adminCoupons() {
        return "admin/coupons";
    }
    @GetMapping("/orders/recent")
    public ResponseEntity<List<OrderDashboardDTO>> getRecentOrders() {
        List<OrderDashboardDTO> recentOrders = orderService.getRecentOrdersForDashboard(10);
        return ResponseEntity.ok(recentOrders);
    }

}